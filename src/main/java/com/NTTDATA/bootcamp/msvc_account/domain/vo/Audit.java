package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public final class Audit {

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Audit(LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Audit create(){
        return new Audit(LocalDateTime.now(), LocalDateTime.now());
    }

    public Audit update(){
        return new Audit(this.createdAt, LocalDateTime.now());
    }

    public static Audit reconstruct(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Audit(createdAt, updatedAt);
    }

}
