package org.example.ndemy_backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ndemy_backend.models.enums.ContentType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(length = 500)
    private String contentUrl;

    @Column(nullable = false, name = "order_index")
    @Builder.Default
    private Integer orderIndex = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="module_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Module module;
}
