package com.deva.dream_shops.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String firstName;
    private String lastName;
    @NaturalId
    private String email;
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user" ,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Order> orders;

    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinTable(name = "user_roles",joinColumns = @JoinColumn(name = "user_id",
            referencedColumnName = "id" ),inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id"))
    private Collection<Role> roles = new HashSet<>();

    //PERSIST - When the parent entity is saved (persist), the child entities are also saved.
    //MERGE - When the parent entity is updated (merge), the child entities are also updated.
    //REMOVE - Deleting the parent entity will delete the associated child entities.
    //REFRESH - Refreshing the parent entity will refresh the associated child entities.
    //DETACH  -  Detaching the parent entity will detach the associated child entities. This means that the child entities are still in the database, but they are no longer attached to the persistence context.

}
