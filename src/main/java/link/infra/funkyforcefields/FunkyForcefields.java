package link.infra.funkyforcefields;

import link.infra.funkyforcefields.blocks.*;
import link.infra.funkyforcefields.regions.ForcefieldFluid;
import link.infra.funkyforcefields.regions.ForcefieldFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FunkyForcefields implements ModInitializer, ClientModInitializer {
	public static final String MODID = "funkyforcefields";

	public static final Block VERTICAL_FORCEFIELD = new VerticalForcefieldBlock(ForcefieldFluids.WATER);
	public static final Block PLASMA_EJECTOR = new PlasmaEjector();

	public static BlockEntityType<PlasmaEjectorBlockEntity> PLASMA_EJECTOR_BLOCK_ENTITY;

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
		new Identifier(MODID, "main"),
		() -> new ItemStack(VERTICAL_FORCEFIELD)
	);

	@Override
	public void onInitialize() {
		Registry.register(Registry.REGISTRIES, new Identifier(MODID, "forcefield_type"), ForcefieldFluid.REGISTRY);
		ForcefieldFluids.register();

		PLASMA_EJECTOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "plasma_ejector"),
			BlockEntityType.Builder.create(PlasmaEjectorBlockEntity::new, PLASMA_EJECTOR).build(null));

		ForcefieldBlocks.registerStandardBlockTypes();

		Registry.register(Registry.BLOCK, new Identifier(MODID, "plasma_ejector"), PLASMA_EJECTOR);
		Registry.register(Registry.ITEM, new Identifier(MODID, "plasma_ejector"),
			new BlockItem(PLASMA_EJECTOR, new Item.Settings().group(ITEM_GROUP)));

		AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
			BlockState state = world.getBlockState(blockPos);
			if (state.getBlock() instanceof ForcefieldBlock) {
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onInitializeClient() {
		// TODO: magically do this on forcefield type registration
		BlockRenderLayerMap.INSTANCE.putBlock(VERTICAL_FORCEFIELD, RenderLayer.getTranslucent());
	}
}
