/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.math.compat;

public class RenderSystem1_15 implements RenderSystem {
    public Class<?> renderSystemClass;
    
    public RenderSystem1_15() {
        try {
            this.renderSystemClass = Class.forName("com.mojang.blaze3d.systems.RenderSystem");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    @Override
    public void color4f(float float_1, float float_2, float float_3, float float_4) {
        try {
            renderSystemClass.getDeclaredMethod("color4f", float.class, float.class, float.class, float.class).invoke(null, float_1, float_2, float_3, float_4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableFog() {
        try {
            renderSystemClass.getDeclaredMethod("disableFog").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableBlend() {
        try {
            renderSystemClass.getDeclaredMethod("enableBlend").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableTexture() {
        try {
            renderSystemClass.getDeclaredMethod("disableTexture").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableTexture() {
        try {
            renderSystemClass.getDeclaredMethod("enableTexture").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableColorLogicOp() {
        try {
            renderSystemClass.getDeclaredMethod("enableColorLogicOp").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableColorLogicOp() {
        try {
            renderSystemClass.getDeclaredMethod("disableColorLogicOp").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableRescaleNormal() {
        try {
            renderSystemClass.getDeclaredMethod("disableRescaleNormal").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void logicOp(int int_1) {
        try {
            renderSystemClass.getDeclaredMethod("logicOp", int.class).invoke(null, int_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void pushMatrix() {
        try {
            renderSystemClass.getDeclaredMethod("pushMatrix").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void popMatrix() {
        try {
            renderSystemClass.getDeclaredMethod("popMatrix").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableLighting() {
        try {
            renderSystemClass.getDeclaredMethod("disableLighting").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableLighting() {
        try {
            renderSystemClass.getDeclaredMethod("enableLighting").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableRescaleNormal() {
        try {
            renderSystemClass.getDeclaredMethod("enableRescaleNormal").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableDepthTest() {
        try {
            renderSystemClass.getDeclaredMethod("disableDepthTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableDepthTest() {
        try {
            renderSystemClass.getDeclaredMethod("enableDepthTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableAlphaTest() {
        try {
            renderSystemClass.getDeclaredMethod("disableAlphaTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableAlphaTest() {
        try {
            renderSystemClass.getDeclaredMethod("enableAlphaTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableBlend() {
        try {
            renderSystemClass.getDeclaredMethod("disableBlend").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void shadeModel(int i) {
        try {
            renderSystemClass.getDeclaredMethod("shadeModel", int.class).invoke(null, i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void colorMask(boolean boolean_1, boolean boolean_2, boolean boolean_3, boolean boolean_4) {
        try {
            renderSystemClass.getDeclaredMethod("colorMask", boolean.class, boolean.class, boolean.class, boolean.class).invoke(null, boolean_1, boolean_2, boolean_3, boolean_4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void translatef(float float_1, float float_2, float float_3) {
        try {
            renderSystemClass.getDeclaredMethod("translatef", float.class, float.class, float.class).invoke(null, float_1, float_2, float_3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void blendFuncSeparate(int int_1, int int_2, int int_3, int int_4) {
        try {
            renderSystemClass.getDeclaredMethod("blendFuncSeparate", int.class, int.class, int.class, int.class).invoke(null, int_1, int_2, int_3, int_4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void blendFunc(int int_1, int int_2) {
        try {
            renderSystemClass.getDeclaredMethod("blendFunc", int.class, int.class).invoke(null, int_1, int_2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
