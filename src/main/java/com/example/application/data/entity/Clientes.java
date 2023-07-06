package com.example.application.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Clientes extends AbstractEntity {

    private Integer cedula;
    private String nombre;
    private String telefono;
    private String direccion;

    public Integer getCedula() {
        return cedula;
    }
    public void setCedula(Integer cedula) {
        this.cedula = cedula;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

}
