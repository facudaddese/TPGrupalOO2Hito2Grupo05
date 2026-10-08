package com.unla.grupo5.entidades.enums;

public enum EnumTurnos {

    MANIANA(0), NOCHE(1);

    private final int numero;

    EnumTurnos(int numero){
        this.numero = numero;
    }

    public int getNumero(){
        return this.numero;
    }

}
