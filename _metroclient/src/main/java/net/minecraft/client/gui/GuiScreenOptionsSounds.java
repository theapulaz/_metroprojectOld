package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiScreenOptionsSounds extends GuiScreen
{
    private final GuiScreen guiScreen;
    private final GameSettings gameSettings;
    protected String field_146507_a = "Options";
    private String field_146508_h;

    public GuiScreenOptionsSounds(GuiScreen guiScreen, GameSettings gameSettings)
    {
        this.guiScreen = guiScreen;
        this.gameSettings = gameSettings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        byte b0 = 0;
        this.field_146507_a = I18n.format("options.sounds.title");
        this.field_146508_h = I18n.format("options.off");
        this.buttonList.add(new GuiScreenOptionsSounds.Button(SoundCategory.MASTER.getCategoryId(), this.width / 2 - 155 + b0 % 2 * 160, this.height / 6 - 12 + 24 * (b0 >> 1), SoundCategory.MASTER, true));
        int k = b0 + 2;
        SoundCategory[] asoundcategory = SoundCategory.values();
        int i = asoundcategory.length;

        for (int j = 0; j < i; ++j)
        {
            SoundCategory soundcategory = asoundcategory[j];

            if (soundcategory != SoundCategory.MASTER)
            {
                this.buttonList.add(new GuiScreenOptionsSounds.Button(soundcategory.getCategoryId(), this.width / 2 - 155 + k % 2 * 160, this.height / 6 - 12 + 24 * (k >> 1), soundcategory, false));
                ++k;
            }
        }

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done", new Object[0])));
    }

    protected void actionPerformed(GuiButton guiButton)
    {
        if (guiButton.enabled)
        {
            if (guiButton.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.guiScreen);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTick)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_146507_a, this.width / 2, 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTick);
    }

    protected String getValue(SoundCategory p_146504_1_)
    {
        float f = this.gameSettings.getSoundLevel(p_146504_1_);
        return f == 0.0F ? this.field_146508_h : (int)(f * 100.0F) + "%";
    }

    @SideOnly(Side.CLIENT)
    class Button extends GuiButton
    {
        private final SoundCategory field_146153_r;
        private final String field_146152_s;
        public float field_146156_o = 1.0F;
        public boolean field_146155_p;
        //private static final String __OBFID = "CL_00000717";

        public Button(int p_i45024_2_, int p_i45024_3_, int p_i45024_4_, SoundCategory p_i45024_5_, boolean p_i45024_6_)
        {
            super(p_i45024_2_, p_i45024_3_, p_i45024_4_, p_i45024_6_ ? 310 : 150, 20, "");
            this.field_146153_r = p_i45024_5_;
            this.field_146152_s = I18n.format("soundCategory." + p_i45024_5_.getCategoryName(), new Object[0]);
            this.displayString = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.getValue(p_i45024_5_);
            this.field_146156_o = GuiScreenOptionsSounds.this.gameSettings.getSoundLevel(p_i45024_5_);
        }

        /**
         * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering
         * over this button.
         */
        public int getHoverState(boolean isHovering)
        {
            return 0;
        }

        /**
         * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
         */
        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
        {
            if (this.visible)
            {
                if (this.field_146155_p)
                {
                    this.field_146156_o = (mouseX - (this.xPosition + 4)) / (this.width - 8);

                    if (this.field_146156_o < 0.0F)
                    {
                        this.field_146156_o = 0.0F;
                    }

                    if (this.field_146156_o > 1.0F)
                    {
                        this.field_146156_o = 1.0F;
                    }

                    mc.gameSettings.setSoundLevel(this.field_146153_r, this.field_146156_o);
                    mc.gameSettings.saveOptions();
                    this.displayString = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.getValue(this.field_146153_r);
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.drawTexturedModalRect(this.xPosition + (int)(this.field_146156_o * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
                this.drawTexturedModalRect(this.xPosition + (int)(this.field_146156_o * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
            }
        }

        /**
         * Returns true if the mouse has been pressed on this control. Equivalent of
         * MouseListener.mousePressed(MouseEvent e).
         */
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
        {
            if (super.mousePressed(mc, mouseX, mouseY))
            {
                this.field_146156_o = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);

                if (this.field_146156_o < 0.0F)
                {
                    this.field_146156_o = 0.0F;
                }

                if (this.field_146156_o > 1.0F)
                {
                    this.field_146156_o = 1.0F;
                }

                mc.gameSettings.setSoundLevel(this.field_146153_r, this.field_146156_o);
                mc.gameSettings.saveOptions();
                this.displayString = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.getValue(this.field_146153_r);
                this.field_146155_p = true;
                return true;
            }
            else
            {
                return false;
            }
        }

        public void playClickSound(SoundHandler sound) {}

        /**
         * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
         */
        public void mouseReleased(int p_146118_1_, int p_146118_2_)
        {
            if (this.field_146155_p)
            {
                if (this.field_146153_r == SoundCategory.MASTER)
                {
                    float f = 1.0F;
                }
                else
                {
                    GuiScreenOptionsSounds.this.gameSettings.getSoundLevel(this.field_146153_r);
                }

                GuiScreenOptionsSounds.this.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
            }

            this.field_146155_p = false;
        }
    }
}