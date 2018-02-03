/* 
 * Nicholas Saney 
 * 
 * Created: February 16, 2015
 * 
 * MultitrackBackgroundAudio.java
 * MultitrackBackgroundAudio class definition
 * 
 */

package chairosoft.quadrado.ui.audio;

import chairosoft.quadrado.ui.system.LifecycleUtility;
import chairosoft.quadrado.util.io.AppendableFileInputStream;
import chairosoft.quadrado.util.ByteConverter;
import chairosoft.quadrado.util.io.Restartable;
import chairosoft.quadrado.util.io.RestartableInputStream;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public abstract class MultitrackBackgroundAudio implements Closeable
{
    // Static Nested Enum
    public static enum TrackState { STOPPED, PAUSED, PLAYING; }
    
    // Static Fields
    public static final int BUFFER_LENGTH_MS = 100;
    public static final String TEMP_PREFIX = "bgAudio_";
    private static ArrayList<MultitrackBackgroundAudio> openBgAudios = new ArrayList<>();
    
    static
    {
        //System.out.println("Static Init of MultitrackBackgroundAudio");
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        //System.out.println("temp dir = " + tempDir);
        File[] files = tempDir.listFiles();
        if (files == null) { files = new File[0]; }
        // int bgAudioTempCount = 0;
        // for (File file : files)
        // {
            // if (file.toString().contains(TEMP_PREFIX)) { bgAudioTempCount++; }
        // }
        //System.out.println("Temp bgAudio file count = " + bgAudioTempCount);
        for (File file : files)
        {
            if (file.toString().contains(TEMP_PREFIX))
            {
                if (file.delete())
                {
                    //System.out.println("Deleted temp file: " + file);
                }
                else
                {
                    System.out.println("Could not delete temp file: " + file);
                }
            }
        }
        
        LifecycleUtility.get().addApplicationCloseHook(new Thread(() -> {
            MultitrackBackgroundAudio[] bgAudiosToClose = MultitrackBackgroundAudio.openBgAudios.toArray(new MultitrackBackgroundAudio[0]);
            for (MultitrackBackgroundAudio bgAudio : bgAudiosToClose)
            {
                try
                {
                    bgAudio.close();
                }
                catch (IOException ioex)
                {
                    System.err.println("Error in shutdown hook closing multitrack background audio.");
                    ioex.printStackTrace();
                }
            }
        }));
    }
    
    // Instance Fields
    protected TrackState state = TrackState.STOPPED;
    public TrackState getState() { return this.state; }
    
    protected Object pauseLock = new Object();
    protected Object closeLock = new Object();
    protected volatile boolean isOpen = false;
    
    protected int mixBufferSize = 0;
    protected float masterVolume = 0;
    protected float[] volumes = null;
    protected ArrayList<RestartableInputStream> restartableInputStreams = new ArrayList<>();
    protected ArrayList<File> temporaryFiles = new ArrayList<>();
    protected int channels = 0;
    protected int bytesPerSample = 0;
    protected int bytesPerFrame = 0;
    protected int samplesPerSecond = 0;
    protected boolean isBigEndian = false;
    protected BufferProcess bufferProcess = null;
    
    
    // Constructor
    protected MultitrackBackgroundAudio() {
        // nothing here right now
    }
    
    
    // Instance Methods
    
    @Override 
    public void close()
        throws IOException
    {
        this.isOpen = false;
        
        if (this.bufferProcess != null)
        {
            this.setMasterVolume(0);
            this.playAll();
            
            synchronized (this.closeLock)
            {
                while (!this.bufferProcess.isFinished)
                {
                    try { this.closeLock.wait(); }
                    catch (InterruptedException ex) { ex.printStackTrace(); }
                }
            }
            
            this.stopAll();
        }
        
        this.mixBufferSize = 0;
        this.masterVolume = 0;
        this.volumes = null;
        this.channels = 0;
        this.bytesPerSample = 0;
        this.bytesPerFrame = 0;
        this.samplesPerSecond = 0;
        this.isBigEndian = false;
        this.bufferProcess = null;
        for (InputStream ris : this.restartableInputStreams) 
        {
            if (ris != null)
            {
                ris.close();
            }
        }
        this.restartableInputStreams.clear();
        for (File file : this.temporaryFiles)
        {
            if (file != null && file.exists())
            {
                if (file.delete())
                {
                    //System.out.println("Deleted temp file: " + file);
                }
                else
                {
                    System.out.println("Could not delete temp file: " + file);
                }
            }
        }
        this.temporaryFiles.clear();
        
        MultitrackBackgroundAudio.openBgAudios.remove(this);
    }
    
    public boolean loadLoopingTracks(long loopStartMillis, long loopEndMillis, String... trackLocations)
    {
        boolean success = false;
        try
        {
            this.close();
            
            // add tracks
            for (int i = 0; i < trackLocations.length; ++i)
            {
                this.loadLoopingTrack(loopStartMillis, loopEndMillis, trackLocations[i], i);
            }
            
            // set up volumes
            this.masterVolume = 1.0f;
            this.volumes = new float[this.restartableInputStreams.size()];
            for (int i = 0; i < this.volumes.length; ++i) { volumes[i] = 1.0f; }
            
            // mark open
            this.isOpen = true;
            MultitrackBackgroundAudio.openBgAudios.add(this);
            
            // post load
            this.postLoad(trackLocations);
            
            // start buffering audio
            this.bufferProcess = this.getNewBufferProcess();
            this.bufferProcess.start();
            
            success = true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            try { this.close(); }
            catch (IOException ex2) { ex2.printStackTrace(); }
        }
        return success;
    }
    
    protected void loadLoopingTrack(long loopStartMillis, long loopEndMillis, String trackLocation, int index) 
        throws Exception
    {
        File tempFile = File.createTempFile(TEMP_PREFIX, null);
        tempFile.deleteOnExit();
        //System.out.println("Temp file created: " + tempFile);
        
        InputStream trackResource = this.getClass().getResourceAsStream(trackLocation);
        PcmSignedInputStream trackResourcePcm = new Mp3ToPcmSignedInputStream(trackResource);
        
        if (index == 0)
        {
            // set audio format properties
            this.channels = trackResourcePcm.getChannels();
            this.bytesPerSample = trackResourcePcm.getBytesPerSample();
            this.bytesPerFrame = this.channels * this.bytesPerSample;
            this.samplesPerSecond = trackResourcePcm.getSamplesPerSecond();
            this.isBigEndian = trackResourcePcm.isBigEndian();
            
            // System.err.println("this.channels = " + this.channels);
            // System.err.println("this.bytesPerSample = " + this.bytesPerSample);
            // System.err.println("this.bytesPerFrame = " + this.bytesPerFrame);
            // System.err.println("this.samplesPerSecond = " + this.samplesPerSecond);
            // System.err.println("this.isBigEndian = " + this.isBigEndian);
            
            // set mix buffer size
            int bytesPerSecond = this.channels * this.bytesPerSample * this.samplesPerSecond;
            int bytesPerMilliSecond = (int)(bytesPerSecond / 1000);
            this.mixBufferSize = bytesPerMilliSecond * MultitrackBackgroundAudio.BUFFER_LENGTH_MS;
        }
        
        AppendableFileInputStream loopTargetStream = new AppendableFileInputStream(tempFile);
        LoopingPcmSignedInputStream pcmStream = new LoopingPcmSignedInputStream(
            trackResourcePcm, 
            loopTargetStream,
            this.channels,
            this.bytesPerSample,
            this.samplesPerSecond,
            this.isBigEndian,
            this.mixBufferSize,
            loopStartMillis, 
            loopEndMillis
        );
        this.restartableInputStreams.add(new RestartableInputStream(pcmStream));
        this.temporaryFiles.add(tempFile);
    }
    
    protected abstract void postLoad(String[] trackLocations) throws Exception;
    
    public void playAll() 
    {
        System.err.println("PLAY ALL");
        synchronized (this.pauseLock)
        {
            this.state = TrackState.PLAYING;
            this.pauseLock.notifyAll();
        }
    }
    
    public void stopAll() 
    {
        System.err.println("STOP ALL");
        synchronized (this.pauseLock)
        {
            this.state = TrackState.STOPPED;
            for (Restartable ris : this.restartableInputStreams)
            {
                try 
                {
                    ris.restart(); 
                }
                catch (IOException ioex) 
                {
                    System.err.println("Error restarting input stream."); 
                    ioex.printStackTrace(); 
                }
            }
        }
    }
    
    public void pauseAll() 
    {
        System.err.println("PAUSE ALL");
        synchronized (this.pauseLock)
        {
            this.state = TrackState.PAUSED;
        }
    }
    
    
    public float getMasterVolume() { return this.masterVolume; }
    public void setMasterVolume(float volume) { this.masterVolume = volume; }
    public float getTrackVolume(int track) { return this.volumes[track]; }
    public void setTrackVolume(int track, float volume) { this.volumes[track] = volume; }
    
    protected abstract BufferProcess getNewBufferProcess();
    protected abstract void writeMixedAudio(byte[] b, int off, int len);
    
    
    // Static Nested Class
    public static abstract class BufferProcess
        extends Thread
    {
        protected final MultitrackBackgroundAudio bgAudio;
        public final ByteConverter byteConverter;
        public final ByteConverter littleEndianByteConverter;
        private boolean isFinished = false;
        public boolean isFinished() { return this.isFinished; }
        
        protected BufferProcess(MultitrackBackgroundAudio _bgAudio)
        {
            this.bgAudio = _bgAudio;
            
            int bytesPerSample = this.bgAudio.bytesPerSample;
            boolean isBigEndian = this.bgAudio.isBigEndian;
            this.byteConverter = new ByteConverter(bytesPerSample, isBigEndian);
            this.littleEndianByteConverter = new ByteConverter(bytesPerSample, false);
        }
        
        protected void setup()
        {
            // does nothing
        }
        
        protected void finish()
        {
            synchronized (this.bgAudio.closeLock)
            {
                this.isFinished = true;
                this.bgAudio.closeLock.notifyAll();
            }
        }
        
        @Override 
        public void run()
        {
            this.setup();
            
            int trackCount = this.bgAudio.volumes.length;
            
            int bufferSize = this.bgAudio.mixBufferSize / 2; // double buffering
            byte[] buffer = new byte[bufferSize];
            
            int bytesPerFrame = this.bgAudio.bytesPerFrame;
            byte[] lineFrameBytes = new byte[bytesPerFrame];
            
            if (bufferSize < bytesPerFrame)
            {
                String message = String.format("Buffer size in bytes (%s) cannot be smaller than frame size in bytes (%s)", bufferSize, bytesPerFrame);
                throw new IllegalArgumentException(message);
            }
            
            int bytesPerSample = this.bgAudio.bytesPerSample;
            int samplesPerFrame = bytesPerFrame / bytesPerSample;
            int[] mixFrameSamples = new int[samplesPerFrame];
            
            boolean isBigEndian = this.bgAudio.isBigEndian;
            
            for (int bufferCount = 0; this.bgAudio.isOpen; ++bufferCount)
            {
                //if (bufferCount % 100 == 0) { System.err.println("Current buffer: " + bufferCount); }
                try
                {
                    synchronized (this.bgAudio.pauseLock)
                    {
                        while (this.bgAudio.state != TrackState.PLAYING)
                        {
                            //System.err.println("Waiting on this.bgAudio.state: " + this.bgAudio.state);
                            try { this.bgAudio.pauseLock.wait(); }
                            catch (InterruptedException ex) { ex.printStackTrace(); }
                        }
                    }
                    
                    for (int frameOffset = 0; frameOffset < bufferSize; frameOffset += bytesPerFrame)
                    {
                        for (int sample = 0; sample < samplesPerFrame; ++sample) 
                        {
                            mixFrameSamples[sample] = 0; 
                        }
                        for (int track = 0; track < trackCount; ++track)
                        {
                            if (this.bgAudio.restartableInputStreams.get(track).read(lineFrameBytes) < 0) { continue; }
                            float volume = this.bgAudio.volumes[track];
                            for (int sample = 0, sampleOffset = 0; sample < samplesPerFrame; ++sample, sampleOffset += bytesPerSample)
                            {
                                // get original sample int from sample bytes
                                int originalSample = this.byteConverter.readInteger(lineFrameBytes, sampleOffset);
                                
                                // multiply original sample int
                                mixFrameSamples[sample] += (int)(volume * originalSample);
                            }
                        }
                        for (int sample = 0, sampleOffset = frameOffset; sample < samplesPerFrame; ++sample, sampleOffset += bytesPerSample)
                        {
                            int rawMixFrameSample = mixFrameSamples[sample];
                            int finalMixFrameSample = this.getFinalizedSample(rawMixFrameSample);
                            this.littleEndianByteConverter.writeInteger(finalMixFrameSample, buffer, sampleOffset);
                        }
                    }
                    
                    this.bgAudio.writeMixedAudio(buffer, 0, bufferSize);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    break;
                }
            }
            
            this.finish();
        }
        
        protected int getFinalizedSample(int rawMixFrameSample)
        {
            // clamp to range
            int result = rawMixFrameSample;
            if (result < this.byteConverter.minValue)
            {
                result = this.byteConverter.minValue;
            }
            else if (result > this.byteConverter.maxValue)
            {
                result = this.byteConverter.maxValue;
            }
            
            // apply master volume
            result = (int)(this.bgAudio.masterVolume * result);
            
            return result;
        }
    }
}