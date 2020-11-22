package com.example.examplemod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("examplemod")
public class ExampleMod {
    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
