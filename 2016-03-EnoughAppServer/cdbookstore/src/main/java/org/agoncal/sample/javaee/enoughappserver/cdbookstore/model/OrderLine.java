package org.agoncal.sample.javaee.enoughappserver.cdbookstore.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "order_line")
public class OrderLine implements Serializable
{

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   private Long id;
   @Version
   @Column(name = "version")
   private int version;

   @Column(nullable = false)
   @Min(1)
   private Integer quantity;

   @ManyToOne(cascade = CascadeType.PERSIST)
   private Item item;

   public Long getId()
   {
      return this.id;
   }

   public void setId(final Long id)
   {
      this.id = id;
   }

   public int getVersion()
   {
      return this.version;
   }

   public void setVersion(final int version)
   {
      this.version = version;
   }

   public Integer getQuantity()
   {
      return quantity;
   }

   public void setQuantity(Integer quantity)
   {
      this.quantity = quantity;
   }

   public Item getItem()
   {
      return this.item;
   }

   public void setItem(final Item item)
   {
      this.item = item;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (!(obj instanceof OrderLine))
      {
         return false;
      }
      OrderLine other = (OrderLine) obj;
      if (id != null)
      {
         if (!id.equals(other.id))
         {
            return false;
         }
      }
      return true;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
   }

   @Override
   public String toString()
   {
      String result = getClass().getSimpleName() + " ";
      if (id != null)
         result += "id: " + id;
      result += ", version: " + version;
      if (quantity != null)
         result += ", quantity: " + quantity;
      return result;
   }
}