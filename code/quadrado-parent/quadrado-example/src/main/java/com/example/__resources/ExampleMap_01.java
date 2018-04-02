package com.example.__resources;

import chairosoft.quadrado.resource.maproom.QMapRoom;
import chairosoft.quadrado.resource.maproom.MapRoomConfig;

public class ExampleMap_01 extends QMapRoom<TileSet_01.TileCode> {
    
    ////// Constants //////
    public static final MapRoomConfig<TileSet_01.TileCode> CONFIG = new MapRoomConfig<>(
        ExampleMap_01::new,
        ExampleMap_01.class
    );
    
    
    ////// Constructor //////
    public ExampleMap_01() {
        super(
            0x00bf7f,
            TileSet_01.CONFIG,
            TileSet_01.TileCode::values,
            links(
                link(12, 0, ExampleMap_02.CONFIG, 3, -2),
                link(13, 0, ExampleMap_02.CONFIG, 4, -2)
            )
        );
    }
    
}
