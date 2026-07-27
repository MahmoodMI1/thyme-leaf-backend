INSERT INTO food_items (name, qty, unit, price) VALUES
                                                    ('Unsalted butter', 5, 'lb', 21.45),
                                                    ('Puff pastry sheets', 10, 'ea', 15.25);

INSERT INTO dishes (name, kind, cost) VALUES
    ('Heirloom tomato tart', 'ENTREES', 0);

INSERT INTO dish_ingredients (dish_id, food_item_id, amount) VALUES
                                                                 (1, 1, 0.5),
                                                                 (1, 2, 4);