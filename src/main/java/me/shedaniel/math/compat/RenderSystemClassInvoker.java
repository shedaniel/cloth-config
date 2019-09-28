/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.math.compat;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RenderSystemClassInvoker implements RenderSystem {
    public Class<?> renderSystemClass;
    
    private Map<String, Method> simpleMethods = new HashMap<>();
    
    public RenderSystemClassInvoker(String className) {
        try {
            this.renderSystemClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    private Method getSimpleMethod(String name, Class... classes) throws NoSuchMethodException {
        if (simpleMethods.containsKey(name))
            return simpleMethods.get(name);
        Method method = renderSystemClass.getDeclaredMethod(name, classes);
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
        try {
            getSimpleMethod("color4f", float.class, float.class, float.class, float.class).invoke(null, float_1, float_2, float_3, float_4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableFog() {
        try {
            getSimpleMethod("disableFog").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableBlend() {
        try {
            getSimpleMethod("enableBlend").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableTexture() {
        try {
            getSimpleMethod("disableTexture").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableTexture() {
        try {
            getSimpleMethod("enableTexture").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableColorLogicOp() {
        try {
            getSimpleMethod("enableColorLogicOp").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableColorLogicOp() {
        try {
            getSimpleMethod("disableColorLogicOp").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableRescaleNormal() {
        try {
            getSimpleMethod("disableRescaleNormal").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void logicOp(int int_1) {
        try {
            getSimpleMethod("logicOp", int.class).invoke(null, int_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void pushMatrix() {
        try {
            getSimpleMethod("pushMatrix").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void popMatrix() {
        try {
            getSimpleMethod("popMatrix").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableLighting() {
        try {
            getSimpleMethod("disableLighting").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableLighting() {
        try {
            getSimpleMethod("enableLighting").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableRescaleNormal() {
        try {
            getSimpleMethod("enableRescaleNormal").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableDepthTest() {
        try {
            getSimpleMethod("disableDepthTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableDepthTest() {
        try {
            getSimpleMethod("enableDepthTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableAlphaTest() {
        try {
            getSimpleMethod("disableAlphaTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void enableAlphaTest() {
        try {
            getSimpleMethod("enableAlphaTest").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void disableBlend() {
        try {
            getSimpleMethod("disableBlend").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void shadeModel(int i) {
        try {
            getSimpleMethod("shadeModel", int.class).invoke(null, i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void colorMask(boolean boolean_1, boolean boolean_2, boolean boolean_3, boolean boolean_4) {
        try {
            getSimpleMethod("colorMask", boolean.class, boolean.class, boolean.class, boolean.class).invoke(null, boolean_1, boolean_2, boolean_3, boolean_4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void translatef(float float_1, float float_2, float float_3) {
        try {
            getSimpleMethod("translatef", float.class, float.class, float.class).invoke(null, float_1, float_2, float_3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void blendFuncSeparate(int int_1, int int_2, int int_3, int int_4) {
        try {
            getSimpleMethod("blendFuncSeparate", int.class, int.class, int.class, int.class).invoke(null, int_1, int_2, int_3, int_4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void blendFunc(int int_1, int int_2) {
        try {
            getSimpleMethod("blendFunc", int.class, int.class).invoke(null, int_1, int_2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
