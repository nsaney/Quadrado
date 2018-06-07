package com.example.__resources.maproom;

import chairosoft.quadrado.resource.maproom.QMapRoom;
import chairosoft.quadrado.resource.maproom.MapRoomConfig;
import com.example.__resources.tileset.TileSet_01;

public class ExampleMap_02 extends QMapRoom<TileSet_01.TileCode> {
    
    ////// Constants //////
    public static final MapRoomConfig<TileSet_01.TileCode> CONFIG = new MapRoomConfig<>(
        ExampleMap_02::new,
        ExampleMap_02.class
    );
    
    
    ////// Constructor //////
    public ExampleMap_02() {
        super(
            0x00bf7f,
            TileSet_01.CONFIG,
            TileSet_01.TileCode::values,
            links(
                link(3, -1, ExampleMap_01.CONFIG, 12, 1),
                link(4, -1, ExampleMap_01.CONFIG, 13, 1)
            )
        );
    }
    
}
