package codes.smit;

import codes.smit.gui.BlacklistScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ItemphobiaClient implements ClientModInitializer {

	public static KeyMapping openGuiKey;
	private static final KeyMapping.Category ITEMPHOBIA_CATEGORY = 
			KeyMapping.Category.register(Identifier.parse("itemphobia:category"));

	@Override
	public void onInitializeClient() {
		openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.itemphobia.open_gui",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_I,
				ITEMPHOBIA_CATEGORY
		));

		Itemphobia.LOGGER.info("Itemphobia client initialized!");
	}

	public static void checkKeyPress() {
		Minecraft mc = Minecraft.getInstance();
		if (openGuiKey != null && openGuiKey.consumeClick() && mc.player != null) {
			mc.setScreen(new BlacklistScreen());
		}
	}
}