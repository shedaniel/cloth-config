/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.math.compat;

public interface RenderSystem {
    
    default void invokeMethod(Class[] classes, Object[] objects) {
        invokeMethod("color4f", classes, objects);
    }
    
    void invokeMethod(String method, Class[] classes, Object[] objects);
    
    void color4f(float float_1, float float_2, float float_3, float float_4);
    
    void enableBlend();
    
    void disableTexture();
    
    void enableTexture();
    
    void enableColorLogicOp();
    
    void disableColorLogicOp();
    
    void disableRescaleNormal();
    
    void logicOp(int int_1);
    
    void pushMatrix();
    
    void disableFog();
    
    void popMatrix();
    
    void disableLighting();
    
    void enableLighting();
    
    void enableRescaleNormal();
    
    void disableDepthTest();
    
    void enableDepthTest();
    
    void disableAlphaTest();
    
    void enableAlphaTest();
    
    void disableBlend();
    
    void shadeModel(int i);
    
    void colorMask(boolean boolean_1, boolean boolean_2, boolean boolean_3, boolean boolean_4);
    
    void translatef(float float_1, float float_2, float float_3);
    
    void blendFuncSeparate(int int_1, int int_2, int int_3, int int_4);
    
    void blendFunc(int int_1, int int_2);
}
