--liquibase formatted sql
--changeset gift-api:3 add-view-with-gift-count

CREATE VIEW child_gift_count_view AS
SELECT c.id AS child_id, COUNT(g.id) AS gift_count
FROM child c
         LEFT JOIN gift g ON c.id = g.child_id
GROUP BY c.id;