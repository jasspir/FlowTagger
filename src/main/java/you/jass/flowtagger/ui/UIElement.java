package you.jass.flowtagger.ui;

import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import you.jass.flowtagger.FlowTagger;

public interface UIElement {
    void render(Object renderer, int mouseX, int mouseY);
    boolean mouseClicked(double mouseX, double mouseY, int button);
    boolean keyPressed(KeyInput input);
    boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);
    boolean mouseReleased(double mouseX, double mouseY, int button);
    default void playSound() {
        //version 1.19.4 - 1.21.10
        //FlowTagger.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));

        //version 1.21.11
        FlowTagger.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1));
    }
}
