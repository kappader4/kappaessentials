package net.kappasmp.kappaessentials;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kappasmp.kappaessentials.bounty.BountyCommand;
import net.kappasmp.kappaessentials.bounty.BountyManager;
import net.kappasmp.kappaessentials.command.*;
import net.kappasmp.kappaessentials.config.ConfigManager;
import net.kappasmp.kappaessentials.economy.BalCommand;
import net.kappasmp.kappaessentials.economy.BalanceManager;
import net.kappasmp.kappaessentials.economy.PayCommand;
import net.kappasmp.kappaessentials.economy.SellCommand;
import net.kappasmp.kappaessentials.homes.HomeCommand;
import net.kappasmp.kappaessentials.teleport.*;
import net.kappasmp.kappaessentials.token.TokenManager;
import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.gui.ShopGui;
import net.kappasmp.kappaessentials.homes.HomeManager;
import net.kappasmp.kappaessentials.token.TokenTopCommand;
import net.kappasmp.kappaessentials.token.TokensCommand;
import net.kappasmp.kappaessentials.update.ModUpdateChecker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.PlaceholderContext;

import java.nio.file.Path;
import java.util.UUID;

import net.fabricmc.loader.api.FabricLoader;

public class KappaEssentials implements ModInitializer {

	public static final String MOD_ID = "kappaessentials";
	private static MinecraftServer serverInstance;

	@Override
	public void onInitialize() {
		// Register all custom commands at once
		registerCommands();

		// Load economy data on server start
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);

		// Save economy data on server stop
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStop);

		log("Mod initialized.");

		BountyManager.loadBounties();

		HomeManager.loadHomes();

		ServerTickEvents.END_SERVER_TICK.register(HomeTeleportScheduler::tick);

		ConfigManager.init(FabricLoader.getInstance().getConfigDir());

		System.out.println("[KappaEssentials] Initialized with config:");
		System.out.println(ConfigManager.getHomeConfig()); // Debug print

		ModUpdateChecker.checkForUpdates("1.1.4");
	}

	private void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			BalCommand.register(dispatcher);
			SellCommand.register(dispatcher);
			ReloadCommand.register(dispatcher);
			AfkCommand.register(dispatcher);
			TokensCommand.register(dispatcher);
			TokenTopCommand.register(dispatcher);
			BalTopCommand.register(dispatcher);
			PayCommand.register(dispatcher);
			BountyCommand.register(dispatcher);
			HomeCommand.register(dispatcher);
			TpaCommand.register(dispatcher);
			TpahereCommand.register(dispatcher);
			TpAcceptCommand.register(dispatcher);
			TpDenyCommand.register(dispatcher);

			// Register /shop command
			dispatcher.register(CommandManager.literal("shop")
					.requires(source -> source.hasPermissionLevel(0))
					.executes(context -> {
						ServerPlayerEntity player = context.getSource().getPlayer();
						new ShopGui(player).open();
						return 1;
					}));
			ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
				ServerPlayerEntity player = handler.player;
				if (player.hasPermissionLevel(2)) {
					ModUpdateChecker.notifyIfOutdated(player, "1.1.5");
				}
			});
		});
	}


	private void onServerStart(MinecraftServer server) {
		serverInstance = server;
		Path configDir = FabricLoader.getInstance().getConfigDir();
		BalanceManager.init(server);
		TokenManager.load(); // Load tokens
		ShopManager.init(configDir); // <<< Load shop.json
		registerPlaceholders();
		log("Balances, tokens, and shop loaded.");
	}

	private void onServerStop(MinecraftServer server) {
		BalanceManager.saveBalances();
		TokenManager.save(); // Save tokens
		ShopManager.saveShop(); // <<< Save shop.json
		log("Balances, tokens, and shop saved.");
	}

	public static MinecraftServer getServerInstance() {
		return serverInstance;
	}

	public static void onPlayerLeave(ServerPlayerEntity player) {
		BalanceManager.savePlayerBalance(player);
	}

	public static void log(String message) {
		System.out.println("[" + MOD_ID + "] " + message);
	}

	private void registerPlaceholders() {
		Placeholders.register(Identifier.of(MOD_ID, "bal"), this::balancePlaceholder);
		Placeholders.register(Identifier.of(MOD_ID, "tokens"), this::tokensPlaceholder); // Register the tokens placeholder
	}

	private PlaceholderResult balancePlaceholder(PlaceholderContext ctx, String arg) {
		if (!ctx.hasPlayer()) {
			return PlaceholderResult.invalid("No player context");
		}
		double balance = BalanceManager.getBalance(ctx.player().getUuid());
		return PlaceholderResult.value(Text.literal(formatBalance(balance)));
	}

	private PlaceholderResult tokensPlaceholder(PlaceholderContext ctx, String arg) {
		if (!ctx.hasPlayer()) {
			return PlaceholderResult.invalid("No player context");
		}
		UUID playerUUID = ctx.player().getUuid();
		int tokens = TokenManager.getTokens(playerUUID);
		return PlaceholderResult.value(Text.literal(formatTokens(tokens)));
	}

	private String formatBalance(double balance) {
		if (balance >= 1_000_000_000) {
			return String.format("%.2fB", balance / 1_000_000_000.0);
		} else if (balance >= 1_000_000) {
			return String.format("%.2fM", balance / 1_000_000.0);
		} else if (balance >= 1_000) {
			return String.format("%.2fK", balance / 1_000.0);
		} else {
			return String.format("%.2f", balance);
		}
	}

	private String formatTokens(int tokens) {
		double value = tokens;
		if (value >= 1_000_000_000) {
			return String.format("%.2fb", value / 1_000_000_000);
		} else if (value >= 1_000_000) {
			return String.format("%.2fm", value / 1_000_000);
		} else if (value >= 1_000) {
			return String.format("%.2fk", value / 1_000);
		} else {
			return String.format("%.2f", value);
		}
	}
}
