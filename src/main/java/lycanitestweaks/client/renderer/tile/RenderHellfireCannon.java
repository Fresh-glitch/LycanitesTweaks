package lycanitestweaks.client.renderer.tile;

import com.lycanitesmobs.client.model.ModelCustom;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.client.LycanitesAssetReloader;
import lycanitestweaks.client.model.item.ModelHellfireCannon;
import lycanitestweaks.handlers.LycanitesTweaksRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderHellfireCannon extends ReloadableModelItemStackRenderer {

    private ModelCustom objModel = null;

    @Override
    public void renderInHand(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(this.getObjModel() != null) {
            this.getObjModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, float partialTicks) {
        if(itemStackIn.getItem() == LycanitesTweaksRegistry.hellfireCannon) {
            this.bindItemTexture(this.getStackTexture(itemStackIn));
            this.renderInHand(
                    Minecraft.getMinecraft().player,
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

    @Override
    public ResourceLocation getStackTexture(ItemStack itemStack) {
        ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStack);
        if(storedCreatureInfo != null) {
            switch (storedCreatureInfo.getEntityVariant()) {
                case 1: return LycanitesAssetReloader.RAHOVART_TEXTURE_1;
                case 2: return LycanitesAssetReloader.RAHOVART_TEXTURE_2;
            }
        }
        return this.getTexture();
    }

    @Override
    public ModelCustom getObjModel() {
        if(this.objModel == null) this.objModel = new ModelHellfireCannon();
        return this.objModel;
    }

    @Override
    public ResourceLocation getTexture() {
        return LycanitesAssetReloader.RAHOVART_TEXTURE_0;
    }

    @Override
    public void onReloadModel() {
        this.objModel = null;
    }
}
