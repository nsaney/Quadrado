package chairosoft.quadrado.game.resource.sprite;

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
        return (++this.currentClick) % this.duration;
    }
    
    public void reset() {
        this.currentClick = 0;
    }
    
}
