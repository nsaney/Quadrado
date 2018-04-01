package com.example.sprite;

import chairosoft.quadrado.game.resource.maproom.QMapRoomExplorerSprite;
import chairosoft.quadrado.game.resource.sprite.*;

public abstract class ExampleSprite01 extends QMapRoomExplorerSprite<
    ExampleSprite01.StateCode,
    ExampleSprite01.BoundingShapeCode,
    ExampleSprite01.AnimationCode
>
{
    public static final SpriteSheetConfig SPRITE_SHEET_CONFIG = new SpriteSheetConfig(
        "SpriteSheet_01",
        0xe0e0e0,
        32,
        32
    );
    
    public enum BoundingShapeCode implements BoundingShapeCodeLiteral<BoundingShapeCode> { box }
    public enum AnimationCode implements AnimationCodeLiteral<AnimationCode> { left, right }
    public enum StateCode implements StateCodeLiteral<StateCode> { left, right }
    
    public ExampleSprite01() {
        super(
            SPRITE_SHEET_CONFIG,
            shapes(
                shape(BoundingShapeCode.box, 8, 16, 16, 16)
            ),
            animations(
                animation(
                    AnimationCode.left,
                    frame(0, 50),
                    frame(2, 50)
                ),
                animation(
                    AnimationCode.right,
                    frame(1, 50),
                    frame(3, 50)
                )
            ),
            states(
                state(StateCode.left, BoundingShapeCode.box, AnimationCode.left),
                state(StateCode.right, BoundingShapeCode.box, AnimationCode.right)
            )
        );
    }
    
}
