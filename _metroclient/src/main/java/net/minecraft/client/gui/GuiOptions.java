package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.stream.GuiStreamOptions;
import net.minecraft.client.gui.stream.GuiStreamUnavailable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.stream.IStream;

@SideOnly(Side.CLIENT)
public class GuiOptions extends GuiScreen implements GuiYesNoCallback
{
    private static final GameSettings.Options[] field_146440_f = new GameSettings.Options[] {GameSettings.Options.FOV, GameSettings.Options.DIFFICULTY};
    private final GuiScreen field_146441_g;
    private final GameSettings field_146443_h;
    protected String field_146442_a = "Options";

    public GuiOptions(GuiScreen p_i1046_1_, GameSettings p_i1046_2_)
    {
        this.field_146441_g = p_i1046_1_;
        this.field_146443_h = p_i1046_2_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        int i = 0;
        this.field_146442_a = I18n.format("options.title");
        GameSettings.Options[] aoptions = field_146440_f;
        int j = aoptions.length;

        for (GameSettings.Options options : aoptions) {
            if (options.getEnumFloat()) {
                this.buttonList.add(new GuiOptionSlider(options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), options));
            } else {
                GuiOptionButton guioptionbutton = new GuiOptionButton(options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), options, this.field_146443_h.getKeyBinding(options));

                if (options == GameSettings.Options.DIFFICULTY && this.mc.theWorld != null && this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                    guioptionbutton.enabled = false;
                    guioptionbutton.displayString = I18n.format("options.difficulty") + ": " + I18n.format("options.difficulty.hardcore");
                }

                this.buttonList.add(guioptionbutton);
            }

            ++i;
        }

        this.buttonList.add(new GuiButton(8675309, this.width / 2f + 5, this.height / 6f + 48 - 6, 150, 20, "Super Secret Settings...")
        {
            public void playClickSound(SoundHandler sound)
            {
                SoundEventAccessorComposite soundeventaccessorcomposite = sound.getRandomSoundFromCategories(SoundCategory.ANIMALS, SoundCategory.BLOCKS, SoundCategory.MOBS, SoundCategory.PLAYERS, SoundCategory.WEATHER);

                if (soundeventaccessorcomposite != null)
                {
                    sound.playSound(PositionedSoundRecord.func_147674_a(soundeventaccessorcomposite.getSoundEventLocation(), 0.5F));
                }
            }
        });
        this.buttonList.add(new GuiButton(106, this.width / 2f - 155, this.height / 6f + 72 - 6, 150, 20, I18n.format("options.sounds")));
        this.buttonList.add(new GuiButton(107, this.width / 2f + 5, this.height / 6f + 72 - 6, 150, 20, I18n.format("options.stream")));
        this.buttonList.add(new GuiButton(101, this.width / 2f - 155, this.height / 6f + 96 - 6, 150, 20, I18n.format("options.video")));
        this.buttonList.add(new GuiButton(100, this.width / 2f + 5, this.height / 6f + 96 - 6, 150, 20, I18n.format("options.controls")));
        this.buttonList.add(new GuiButton(102, this.width / 2f - 155, this.height / 6f + 120 - 6, 150, 20, I18n.format("options.language")));
        this.buttonList.add(new GuiButton(103, this.width / 2f + 5, this.height / 6f + 120 - 6, 150, 20, I18n.format("options.multiplayer.title")));
        this.buttonList.add(new GuiButton(105, this.width / 2f - 155, this.height / 6f + 144 - 6, 150, 20, I18n.format("options.resourcepack")));
        this.buttonList.add(new GuiButton(104, this.width / 2f + 5, this.height / 6f + 144 - 6, 150, 20, I18n.format("options.snooper.view")));
        this.buttonList.add(new GuiButton(200, this.width / 2f - 100, this.height / 6f + 168, I18n.format("gui.done")));
    }

    protected void actionPerformed(GuiButton guiButton)
    {
        if (guiButton.enabled)
        {
            if (guiButton.id < 100 && guiButton instanceof GuiOptionButton)
            {
                this.field_146443_h.setOptionValue(((GuiOptionButton) guiButton).returnEnumOptions(), 1);
                guiButton.displayString = this.field_146443_h.getKeyBinding(GameSettings.Options.getEnumOptions(guiButton.id));
            }

            if (guiButton.id == 8675309)
            {
                this.mc.entityRenderer.activateNextShader();
            }

            if (guiButton.id == 101)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiVideoSettings(this, this.field_146443_h));
            }

            if (guiButton.id == 100)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiControls(this, this.field_146443_h));
            }

            if (guiButton.id == 102)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiLanguage(this, this.field_146443_h, this.mc.getLanguageManager()));
            }

            if (guiButton.id == 103)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new ScreenChatOptions(this, this.field_146443_h));
            }

            if (guiButton.id == 104)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiSnooper(this, this.field_146443_h));
            }

            if (guiButton.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.field_146441_g);
            }

            if (guiButton.id == 105)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiScreenResourcePacks(this));
            }

            if (guiButton.id == 106)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiScreenOptionsSounds(this, this.field_146443_h));
            }

            if (guiButton.id == 107)
            {
                this.mc.gameSettings.saveOptions();
                IStream istream = this.mc.func_152346_Z();

                if (istream.func_152936_l() && istream.func_152928_D())
                {
                    this.mc.displayGuiScreen(new GuiStreamOptions(this, this.field_146443_h));
                }
                else
                {
                    GuiStreamUnavailable.func_152321_a(this);
                }
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTick)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_146442_a, this.width / 2f, 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTick);
    }
}