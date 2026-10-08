package com.unla.grupo5.entities.enums;

public enum EnumRoles {

    ADMIN(0), RESPONSABLE(1), EMPLEADO(2);

    private final int numero;

    EnumRoles(int numero){
        this.numero = numero;
    }

    public int getNumero(){
        return this.numero;
    }

}
