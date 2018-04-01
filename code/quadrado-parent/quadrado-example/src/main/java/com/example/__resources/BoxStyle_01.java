package com.example.__resources;

import chairosoft.quadrado.game.resource.box_style.QBoxStyle;

public class BoxStyle_01 extends QBoxStyle {
    
    public BoxStyle_01() {
        super(
            0xff00ff,
            borderWidths(8, 8, 8, 8),
            borderLengths(1, 1),
            arrow(17, 0, 8, 8),
            arrow(25, 0, 8, 8),
            arrow(17, 8, 8, 8),
            arrow(25, 8, 8, 8)
        );
    }
}
