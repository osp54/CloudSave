package com.ospx.cloudsavemod.models;


import arc.math.Rand;

import java.time.LocalDateTime;

public class Save {
    public String _id;
    public LocalDateTime date;

    @SuppressWarnings("unused")
    public Save() {
    }

    // For testing purposes only
    @SuppressWarnings("unused")
    public Save(boolean nya) {
        this._id = String.valueOf(new Rand().nextInt(9999));
        this.date = LocalDateTime.now();
    }
}