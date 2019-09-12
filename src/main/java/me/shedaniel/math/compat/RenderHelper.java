/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.math.compat;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class RenderHelper {
    
    static RenderSystem instance;
    
    static {
        boolean is1_15 = false;
        try {
            Class.forName("com.mojang.blaze3d.systems.RenderSystem");
            is1_15 = true;
        } catch (ClassNotFoundException ignored) {
        }
        if (!is1_15)
            try {
                instance = (RenderSystem) Class.forName("me.shedaniel.math.compat.RenderSystem1_14").newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            if (is1_15)
                instance = new RenderSystemClassInvoker("com.mojang.blaze3d.systems.RenderSystem");
            else instance = new RenderSystemClassInvoker("com.mojang.blaze3d.platform.GlStateManager");
        } catch (Exception e) {
            CrashReport crashReport = CrashReport.create(e, "Creating Blaze3D Classes Compat");
            CrashReportSection section = crashReport.addElement("Found Render System");
            boolean finalIs1_15 = is1_15;
            section.add("Is 1.15", () -> finalIs1_15 + "");
            throw new CrashException(crashReport);
        }
    }
    
    public static void color4f(float float_1, float float_2, float float_3, float float_4) {
        instance.color4f(float_1, float_2, float_3, float_4);
    }
    
    public static void enableBlend() {
        instance.enableBlend();
    }
    
    public static void disableTexture() {
        instance.disableTexture();
    }
    
    public static void enableTexture() {
        instance.enableTexture();
    }
    
    public static void enableColorLogicOp() {
        instance.enableColorLogicOp();
    }
    
    public static void disableColorLogicOp() {
        instance.disableColorLogicOp();
    }
    
    public static void disableRescaleNormal() {
        instance.disableRescaleNormal();
    }
    
    public static void logicOp(int int_1) {
        instance.logicOp(int_1);
    }
    
    public static void pushMatrix() {
        instance.pushMatrix();
    }
    
    public static void disableFog() {
        instance.disableFog();
    }
    
    public static void popMatrix() {
        instance.popMatrix();
    }
    
    public static void disableLighting() {
        instance.disableLighting();
    }
    
    public static void enableLighting() {
        instance.enableLighting();
    }
    
    public static void enableRescaleNormal() {
        instance.enableRescaleNormal();
    }
    
    public static void disableDepthTest() {
        instance.disableDepthTest();
    }
    
    public static void enableDepthTest() {
        instance.enableDepthTest();
    }
    
    public static void disableAlphaTest() {
        instance.disableAlphaTest();
    }
    
    public static void enableAlphaTest() {
        instance.enableAlphaTest();
    }
    
    public static void disableBlend() {
        instance.disableBlend();
    }
    
    public static void shadeModel(int i) {
        instance.shadeModel(i);
    }
    
    public static void colorMask(boolean boolean_1, boolean boolean_2, boolean boolean_3, boolean boolean_4) {
        instance.colorMask(boolean_1, boolean_2, boolean_3, boolean_4);
    }
    
    public static void translatef(float float_1, float float_2, float float_3) {
        instance.translatef(float_1, float_2, float_3);
    }
    
    public static void blendFuncSeparate(int int_1, int int_2, int int_3, int int_4) {
        instance.blendFuncSeparate(int_1, int_2, int_3, int_4);
    }
    
    public static void blendFunc(int int_1, int int_2) {
        instance.blendFunc(int_1, int_2);
    }
}
