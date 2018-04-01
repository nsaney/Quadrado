package com.example.__resources;

import chairosoft.quadrado.game.resource.maproom.QMapRoom;
import chairosoft.quadrado.game.resource.maproom.MapRoomConfig;

public class ExampleMap_01 extends QMapRoom<TileSet_01.TileCode> {
    
    ////// Constants //////
    public static final MapRoomConfig<TileSet_01.TileCode> MAP_ROOM_CONFIG = new MapRoomConfig<>(
        ExampleMap_01.class,
        TileSet_01.TileCode::values,
        0x00bf7f
    );
    
    
    ////// Constructor //////
    public ExampleMap_01() {
        super(
            MAP_ROOM_CONFIG,
            TileSet_01::new,
            links(
                link(12,0, ExampleMap_02::new, 3, -2),
                link(13,0, ExampleMap_02::new, 4, -2)
            )
        );
    }
    
}
