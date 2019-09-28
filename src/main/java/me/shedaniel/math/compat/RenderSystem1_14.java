/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.math.compat;

import com.mojang.blaze3d.platform.GlStateManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RenderSystem1_14 implements RenderSystem {
    
    private Map<String, Method> simpleMethods = new HashMap<>();
    
    private Method getSimpleMethod(String name, Class... classes) throws NoSuchMethodException {
        if (simpleMethods.containsKey(name))
            return simpleMethods.get(name);
        Method method = GlStateManager.class.getDeclaredMethod(name, classes);
        simpleMethods.put(name, method);
        return simpleMethods.get(name);
    }
    
    @Override
    public void invokeMethod(String method, Class[] classes, Object[] objects) {
        try {
            getSimpleMethod(method, classes).invoke(null, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void color4f(float float_1, float float_2, float float_3, float float_4) {
        GlStateManager.color4f(float_1, float_2, float_3, float_4);
    }
    
    @Override
    public void enableBlend() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void disableTexture() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void enableTexture() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void enableColorLogicOp() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void disableColorLogicOp() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void disableRescaleNormal() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void disableFog() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void logicOp(int int_1) {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void pushMatrix() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void popMatrix() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void disableLighting() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void enableLighting() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void enableRescaleNormal() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void disableDepthTest() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void enableDepthTest() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void disableAlphaTest() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void enableAlphaTest() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void disableBlend() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void shadeModel(int i) {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void colorMask(boolean boolean_1, boolean boolean_2, boolean boolean_3, boolean boolean_4) {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void translatef(float float_1, float float_2, float float_3) {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void blendFuncSeparate(int int_1, int int_2, int int_3, int int_4) {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void blendFunc(int int_1, int int_2) {
        GlStateManager.enableBlend();
    }
}
