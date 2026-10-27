package ru.practicum.shareit.comment;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Entity
@Table(name = "comments")
@Data
@EqualsAndHashCode(of = "id")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item commentItem;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
}
