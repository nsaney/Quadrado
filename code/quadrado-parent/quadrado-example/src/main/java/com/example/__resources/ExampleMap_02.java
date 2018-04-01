package com.example.__resources;

import chairosoft.quadrado.game.resource.maproom.QMapRoom;
import chairosoft.quadrado.game.resource.maproom.MapRoomConfig;

public class ExampleMap_02 extends QMapRoom<TileSet_01.TileCode> {
    
    ////// Constants //////
    public static final MapRoomConfig<TileSet_01.TileCode> MAP_ROOM_CONFIG = new MapRoomConfig<>(
        ExampleMap_02.class,
        TileSet_01.TileCode::values,
        0x00bf7f
    );
    
    
    ////// Constructor //////
    public ExampleMap_02() {
        super(
            MAP_ROOM_CONFIG,
            TileSet_01::new,
            links(
                link(3, -1, ExampleMap_01::new, 12, 1),
                link(4, -1, ExampleMap_01::new, 13, 1)
            )
        );
    }
    
}
