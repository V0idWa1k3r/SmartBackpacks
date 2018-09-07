package v0id.api.vsb.item;

public enum EnumBackpackType
{
    BASIC(18, 5),
    REINFORCED(36, 9),
    ADVANCED(54, 14),
    ULTIMATE(117, 18);

    EnumBackpackType(int inventorySize, int upgradesSize)
    {
        this.inventorySize = inventorySize;
        this.upgradesSize = upgradesSize;
    }

    private final int inventorySize;
    private final int upgradesSize;

    public int getInventorySize()
    {
        return inventorySize;
    }

    public int getUpgradesSize()
    {
        return upgradesSize;
    }
}
