package lycanitestweaks.client.renderer.tile;

import com.lycanitesmobs.client.model.ModelCustom;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.client.LycanitesAssetReloader;
import lycanitestweaks.client.model.item.ModelDevilGatlingGun;
import lycanitestweaks.client.renderer.RenderContext;
import lycanitestweaks.handlers.LycanitesTweaksRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderDevilGatlingGun extends ReloadableModelItemStackRenderer {

    private ModelCustom objModel = null;

    @Override
    public void renderInHand(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(this.getObjModel() != null) {
            this.getObjModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, float partialTicks) {
        if(itemStackIn.getItem() == LycanitesTweaksRegistry.devilGatlingGun) {
            EntityLivingBase entityLivingBase = RenderContext.currentRenderEntity;

            this.bindItemTexture(this.getStackTexture(itemStackIn));

            if(entityLivingBase != null && itemStackIn == RenderContext.currentRenderStack) {
                this.renderInHand(
                        entityLivingBase,
                        entityLivingBase.limbSwing,
                        entityLivingBase.limbSwingAmount,
                        partialTicks,
                        entityLivingBase.ticksExisted,
                        0F,
                        0F,
                        0.0625F
                );
            }
            else {
                this.renderInHand(
                        null,
                        Minecraft.getMinecraft().player.limbSwing,
                        Minecraft.getMinecraft().player.limbSwingAmount,
                        partialTicks,
                        Minecraft.getMinecraft().player.ticksExisted,
                        0F,
                        0F,
                        0.0625F
                );
            }
        }
    }

    @Override
    public ResourceLocation getStackTexture(ItemStack itemStack) {
        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
        if(storedCreatureInfo != null) {
            switch (storedCreatureInfo.getEntityVariant()) {
                case 1: return LycanitesAssetReloader.ASMODEUS_TEXTURE_1;
                case 2: return LycanitesAssetReloader.ASMODEUS_TEXTURE_2;
            }
        }
        return this.getTexture();
    }

    @Override
    public ModelCustom getObjModel() {
        if(this.objModel == null) this.objModel = new ModelDevilGatlingGun();
        return this.objModel;
    }

    @Override
    public ResourceLocation getTexture() {
        return LycanitesAssetReloader.ASMODEUS_TEXTURE_0;
    }

    @Override
    public void onReloadModel() {
        this.objModel = null;
    }
}
