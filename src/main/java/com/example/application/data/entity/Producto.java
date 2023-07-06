package com.example.application.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Producto extends AbstractEntity {

    private Integer idProducto;
    private String nombre;
    private Integer precio;
    private Integer stock;
    private String proovedor;

    public Integer getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Integer getPrecio() {
        return precio;
    }
    public void setPrecio(Integer precio) {
        this.precio = precio;
    }
    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public String getProovedor() {
        return proovedor;
    }
    public void setProovedor(String proovedor) {
        this.proovedor = proovedor;
    }

}
