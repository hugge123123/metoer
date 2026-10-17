package dev.escoter.stafflookalert;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class StaffLookAlertAddon extends MeteorAddon {
    @Override
    public void onInitialize() {
        Modules.get().add(new StaffLookAlert());
    }

    @Override
    public String getPackage() {
        return "dev.escoter.stafflookalert";
    }

    @Override
    public void onRegisterCategories() {
    }
}
