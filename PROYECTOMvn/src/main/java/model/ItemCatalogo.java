/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author PC
 */
public class ItemCatalogo
{

    private final int id;
    private final String nombre;

    public ItemCatalogo(int id, String nombre)
    {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId()
    {
        return id;
    }

    public String getNombre()
    {
        return nombre;
    }

    @Override
    public String toString()
    {
        return nombre;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ItemCatalogo))
        {
            return false;
        }
        return id == ((ItemCatalogo) o).id;
    }

    @Override
    public int hashCode()
    {
        return Integer.hashCode(id);
    }

}
