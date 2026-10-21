package lycanitestweaks.client.renderer.tile;

import com.lycanitesmobs.client.model.ModelCustom;
import lycanitestweaks.capability.storedcreatureinfo.ItemStoredCreatureInfo;
import lycanitestweaks.capability.toggleableitem.IToggleableItem;
import lycanitestweaks.capability.toggleableitem.ToggleableItem;
import lycanitestweaks.client.LycanitesAssetReloader;
import lycanitestweaks.client.model.item.ModelHellShield;
import lycanitestweaks.client.renderer.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderHellShield extends ReloadableModelItemStackRenderer {

    private ModelHellShield objModel = null;

    @Override
    public void renderInHand(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(this.getObjModel() != null) {
            this.getObjModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, float partialTicks) {
        if(itemStackIn != RenderContext.currentRenderStack) return;
        EntityLivingBase entityLivingBase = RenderContext.currentRenderEntity;;

        if(this.objModel != null) {
            IToggleableItem toggleableItem = ToggleableItem.getForItemStack(itemStackIn);
            if(toggleableItem != null) {
                if(toggleableItem.isAbilityToggled()) {
                    ItemStoredCreatureInfo storedCreatureInfo = ItemStoredCreatureInfo.getForItemStack(itemStackIn);
                    if(storedCreatureInfo != null) {
                        this.objModel.setShieldVariant(storedCreatureInfo.getEntityVariant());
                    }
                    this.objModel.setShieldActive(true);
                }
                else {
                    this.objModel.setShieldActive(false);
                }
            }
            else {
                this.objModel.setShieldActive(false);
            }
        }

        this.bindItemTexture(this.getStackTexture(itemStackIn));
        if(entityLivingBase != null) {
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
        if(this.objModel == null) this.objModel = new ModelHellShield();
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
