package codes.smit;

import codes.smit.gui.BlacklistScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ItemphobiaClient implements ClientModInitializer {

	public static KeyMapping openGuiKey;

	@Override
	public void onInitializeClient() {
		// Register keybind
		openGuiKey = new KeyMapping(
				"key.itemphobia.open_gui",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_I,
				"category.itemphobia"
		);

		Itemphobia.LOGGER.info("Itemphobia client initialized!");
	}

	public static void checkKeyPress() {
		Minecraft mc = Minecraft.getInstance();
		if (openGuiKey != null && openGuiKey.consumeClick() && mc.player != null) {
			mc.setScreen(new BlacklistScreen());
		}
	}
}