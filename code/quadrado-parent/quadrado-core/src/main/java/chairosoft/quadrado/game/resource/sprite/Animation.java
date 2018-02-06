package chairosoft.quadrado.game.resource.sprite;

import chairosoft.quadrado.game.resource.literals.EnumCodedObjectImpl;

public class Animation<A extends Enum<A> & AnimationCodeLiteral<A>> extends EnumCodedObjectImpl<A> {
    
    ////// Instance Fields //////
    public final AnimationFrame[] frames;
    protected int currentFrameIndex = 0;
    protected int repeatCount = 0;
    
    
    ////// Constructor //////
    protected Animation(A _code, AnimationFrame... _frames) {
        super(_code);
        this.frames = _frames;
    }
    
    
    ////// Instance Methods //////
    public AnimationFrame getCurrentFrame() {
        return this.frames[this.currentFrameIndex];
    }
    
    public int advanceOneClickAndGetRepeatCount() {
        AnimationFrame currentFrame = this.frames[this.currentFrameIndex];
        int currentFrameClick = currentFrame.advanceOneClick();
        if (0 == currentFrameClick) {
            this.currentFrameIndex = (this.currentFrameIndex + 1) % this.frames.length;
            //this.frames[this.currentFrameIndex].reset(); // this may not be necessary
            if (0 == this.currentFrameIndex) {
                ++this.repeatCount;
            }
        }
        return this.repeatCount;
    }
    
    public void reset() {
        this.frames[this.currentFrameIndex].reset();
        this.currentFrameIndex = 0;
        this.repeatCount = 0;
    }
}
