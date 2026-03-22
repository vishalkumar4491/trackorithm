package com.trackorithm.track.modules.tracking.entity;

import com.trackorithm.track.modules.problem.entity.Problem;
import com.trackorithm.track.modules.tag.entity.UserTag;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_problem_tags")
@Getter
@Setter
public class UserProblemTag {

    @EmbeddedId
    private UserProblemTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("problemId")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private UserTag tag;
}
