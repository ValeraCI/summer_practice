package com.ledivax.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "roles")
@Getter
@Setter
public class Role extends AEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "title")
    private RoleTitle roleTitle;

    public Role(Long id, RoleTitle roleTitle) {
        super(id);
        this.roleTitle = roleTitle;
    }
}
