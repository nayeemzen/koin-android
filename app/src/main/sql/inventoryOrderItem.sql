CREATE TABLE InventoryOrderItem (
    order_item_id INTEGER PRIMARY KEY NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_id TEXT NOT NULL,
    item_price INTEGER NOT NULL,
    item_quantity INTEGER NOT NULL,
    item_name TEXT NOT NULL,
    item_description TEXT NOT NULL,
    item_image_url TEXT,
    item_additional_notes TEXT
);