package insyncwithfoo.ryecharm.uv.run

import insyncwithfoo.ryecharm.RyeCharm


/**
 * IDs to be used by run configuration types and factories.
 * 
 * These IDs are persisted by the IDE.
 * As such, any changes to them will necessarily be backward-incompatible.
 */
internal object UVRunIDs {
    
    const val MAIN_TYPE = "${RyeCharm.ID}.uv"
    
    const val CUSTOM_FACTORY = "${MAIN_TYPE}.custom"
    
}
