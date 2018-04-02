package chairosoft.quadrado.resource.tileset;

import chairosoft.quadrado.resource.literals.EnumLiteral;

import java.util.HashMap;
import java.util.Map;

public interface TileCodeLiteral<T extends Enum<T> & TileCodeLiteral<T>> extends EnumLiteral<T> {
    
    ////// Instance Methods //////
    String getAlternateCode();
    
    
    ////// Static Methods //////
    static <T extends Enum<T> & TileCodeLiteral<T>> Map<String, T> getTileCodesByAlternateCode(T[] values) {
        Map<String, T> result = new HashMap<>();
        for (T tileCode : values) {
            String alternateCode = tileCode.getAlternateCode();
            if (alternateCode == null) { continue; }
            result.put(alternateCode, tileCode);
        }
        return result;
    }
    
}
