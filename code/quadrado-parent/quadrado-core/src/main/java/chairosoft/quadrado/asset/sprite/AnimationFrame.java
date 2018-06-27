package chairosoft.quadrado.asset.sprite;

public class AnimationFrame {
    
    ////// Instance Fields //////
    public final int imageIndex;
    public final int duration;
    protected int currentClick = 0;
    
    
    ////// Constructor //////
    protected AnimationFrame(int _imageIndex, int _duration) {
        this.imageIndex = _imageIndex;
        this.duration = _duration;
    }
    
    
    ////// Instance Methods //////
    public int advanceOneClick() {
        this.currentClick = (this.currentClick + 1) % this.duration;
        return this.currentClick;
    }
    
    public void reset() {
        this.currentClick = 0;
    }
    
}
