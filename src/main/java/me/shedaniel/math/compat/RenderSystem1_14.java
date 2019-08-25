/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.math.compat;

public class RenderSystem1_14 implements RenderSystem {
    
    public Class<?> glStateManagerClass;
    
    public RenderSystem1_14() {
        try {
            this.glStateManagerClass = Class.forName("com.mojang.blaze3d.platform.GlStateManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    @Override
    public void color4f(float float_1, float float_2, float float_3, float float_4) {
        try {
            glStateManagerClass.getDeclaredMethod("color4f", float.class, float.class, float.class, float.class).invoke(null, float_1, float_2, float_3, float_4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableBlend() {
        try {
            glStateManagerClass.getDeclaredMethod("enableBlend").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableTexture() {
        try {
            glStateManagerClass.getDeclaredMethod("disableTexture").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableTexture() {
        try {
            glStateManagerClass.getDeclaredMethod("enableTexture").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableColorLogicOp() {
        try {
            glStateManagerClass.getDeclaredMethod("enableColorLogicOp").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableColorLogicOp() {
        try {
            glStateManagerClass.getDeclaredMethod("disableColorLogicOp").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableRescaleNormal() {
        try {
            glStateManagerClass.getDeclaredMethod("disableRescaleNormal").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableFog() {
        try {
            glStateManagerClass.getDeclaredMethod("disableFog").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void logicOp(int int_1) {
        try {
            glStateManagerClass.getDeclaredMethod("logicOp", int.class).invoke(null, int_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void pushMatrix() {
        try {
            glStateManagerClass.getDeclaredMethod("pushMatrix").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void popMatrix() {
        try {
            glStateManagerClass.getDeclaredMethod("popMatrix").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableLighting() {
        try {
            glStateManagerClass.getDeclaredMethod("disableLighting").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableLighting() {
        try {
            glStateManagerClass.getDeclaredMethod("enableLighting").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableRescaleNormal() {
        try {
            glStateManagerClass.getDeclaredMethod("enableRescaleNormal").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableDepthTest() {
        try {
            glStateManagerClass.getDeclaredMethod("disableDepthTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableDepthTest() {
        try {
            glStateManagerClass.getDeclaredMethod("enableDepthTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableAlphaTest() {
        try {
            glStateManagerClass.getDeclaredMethod("disableAlphaTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableAlphaTest() {
        try {
            glStateManagerClass.getDeclaredMethod("enableAlphaTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableBlend() {
        try {
            glStateManagerClass.getDeclaredMethod("disableBlend").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void shadeModel(int i) {
        try {
            glStateManagerClass.getDeclaredMethod("shadeModel", int.class).invoke(null, i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void colorMask(boolean boolean_1, boolean boolean_2, boolean boolean_3, boolean boolean_4) {
        try {
            glStateManagerClass.getDeclaredMethod("colorMask", boolean.class, boolean.class, boolean.class, boolean.class).invoke(null, boolean_1, boolean_2, boolean_3, boolean_4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void translatef(float float_1, float float_2, float float_3) {
        try {
            glStateManagerClass.getDeclaredMethod("translatef", float.class, float.class, float.class).invoke(null, float_1, float_2, float_3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void blendFuncSeparate(int int_1, int int_2, int int_3, int int_4) {
        try {
            glStateManagerClass.getDeclaredMethod("blendFuncSeparate", int.class, int.class, int.class, int.class).invoke(null, int_1, int_2, int_3, int_4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void blendFunc(int int_1, int int_2) {
        try {
            glStateManagerClass.getDeclaredMethod("blendFunc", int.class, int.class).invoke(null, int_1, int_2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
