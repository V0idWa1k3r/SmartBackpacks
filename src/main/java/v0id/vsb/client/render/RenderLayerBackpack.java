package v0id.vsb.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.api.vsb.data.VSBTextures;
import v0id.vsb.client.model.ModelBackpack;
import v0id.vsb.client.model.ModelBackpackAdvanced;
import v0id.vsb.client.model.ModelBackpackReinforced;
import v0id.vsb.client.model.ModelBackpackUltimate;

public class RenderLayerBackpack implements LayerRenderer<EntityPlayer>
{
    public ModelBase modelBackpack;
    public ModelBase modelBackpackReinforced;
    public ModelBase modelBackpackAdvanced;
    public ModelBase modelBackpackUltimate;

    public RenderLayerBackpack()
    {
        this.modelBackpack = new ModelBackpack();
        this.modelBackpackReinforced = new ModelBackpackReinforced();
        this.modelBackpackAdvanced = new ModelBackpackAdvanced();
        this.modelBackpackUltimate = new ModelBackpackUltimate();
    }

    @Override
    public void doRenderLayer(EntityPlayer playerIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        IVSBPlayer player = IVSBPlayer.of(playerIn);
        if (player != null && !player.getCurrentBackpack().isEmpty())
        {
            IBackpack backpack = IBackpack.of(player.getCurrentBackpack());
            if (backpack != null)
            {
                ModelBase model = null;
                switch (backpack.createWrapper().getBackpackType())
                {
                    case BASIC:
                    {
                        Minecraft.getMinecraft().renderEngine.bindTexture(VSBTextures.MODEL_BACKPACK_BASIC);
                        model = this.modelBackpack;
                        break;
                    }

                    case REINFORCED:
                    {
                        Minecraft.getMinecraft().renderEngine.bindTexture(VSBTextures.MODEL_BACKPACK_REINFORCED);
                        model = this.modelBackpackReinforced;
                        break;
                    }

                    case ADVANCED:
                    {
                        Minecraft.getMinecraft().renderEngine.bindTexture(VSBTextures.MODEL_BACKPACK_ADVANCED);
                        model = this.modelBackpackAdvanced;
                        break;
                    }

                    case ULTIMATE:
                    {
                        Minecraft.getMinecraft().renderEngine.bindTexture(VSBTextures.MODEL_BACKPACK_UlTIMATE);
                        model = this.modelBackpackUltimate;
                        break;
                    }
                }

                int color = backpack.createWrapper().getColor();
                float r = ((color & 0xFF0000) >> 16) / 255F;
                float g = ((color & 0xFF00) >> 8) / 255F;
                float b = (color & 0xFF) / 255F;
                GlStateManager.pushMatrix();
                GlStateManager.color(r, g, b);
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(0, 0.5F, -0.3F);
                if (playerIn.isSneaking())
                {
                    GlStateManager.rotate(-30, 1, 0, 0);
                    GlStateManager.translate(0, 0, -0.2F);
                }

                model.render(playerIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GlStateManager.color(1, 1, 1, 1F);
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}
