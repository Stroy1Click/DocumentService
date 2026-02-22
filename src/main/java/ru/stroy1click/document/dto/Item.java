package ru.stroy1click.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {

    String name;

    Double price;

    Integer quantity;
}
