USE warehouse_manager;

INSERT INTO locations (code, aisle, side, position, x_coordinate, y_coordinate, max_capacity, active) VALUES
('A-1-01','A',1,1,2,2,200,TRUE),('A-1-02','A',1,2,2,4,180,TRUE),('A-2-01','A',2,1,4,2,200,TRUE),
('B-1-01','B',1,1,7,2,250,TRUE),('B-2-03','B',2,3,9,6,160,TRUE),('C-1-02','C',1,2,12,4,220,TRUE),
('C-2-04','C',2,4,14,8,180,TRUE),('D-1-01','D',1,1,17,2,240,TRUE),('D-2-03','D',2,3,19,6,190,TRUE),
('E-1-02','E',1,2,22,4,200,TRUE),('E-2-05','E',2,5,24,10,150,TRUE),('E-2-06','E',2,6,24,12,100,FALSE)
ON DUPLICATE KEY UPDATE code=VALUES(code);

INSERT INTO products (sku,name,description,category,quantity,minimum_stock,location_id,active) VALUES
('BOX-S-001','Small shipping boxes','Single-wall cardboard boxes','Packaging',120,30,(SELECT id FROM locations WHERE code='A-1-01'),TRUE),
('TAPE-CLR-48','Clear packing tape 48 mm','Industrial packing tape','Packaging',18,20,(SELECT id FROM locations WHERE code='A-1-02'),TRUE),
('GLOVE-M-100','Safety gloves size M','Protective work gloves','Safety',45,15,(SELECT id FROM locations WHERE code='B-1-01'),TRUE),
('LABEL-100X150','Thermal labels 100 x 150','Rolls for shipping labels','Labels',8,10,(SELECT id FROM locations WHERE code='B-2-03'),TRUE),
('SCANNER-HH-01','Handheld barcode scanner','USB barcode scanner','Equipment',12,3,(SELECT id FROM locations WHERE code='C-1-02'),TRUE),
('WRAP-500','Stretch wrap 500 mm','Transparent pallet wrap','Packaging',64,12,(SELECT id FROM locations WHERE code='C-2-04'),TRUE),
('VEST-YEL-L','High visibility vest L','Yellow warehouse safety vest','Safety',6,8,(SELECT id FROM locations WHERE code='D-1-01'),TRUE),
('PALLET-EUR','EUR pallet','Reusable wooden pallet','Pallets',38,10,(SELECT id FROM locations WHERE code='D-2-03'),TRUE),
('CUTTER-SAFE','Safety box cutter','Retractable warehouse cutter','Tools',22,5,(SELECT id FROM locations WHERE code='E-1-02'),TRUE),
('MARKER-BLK','Permanent marker black','Warehouse labelling marker','Tools',4,12,(SELECT id FROM locations WHERE code='E-2-05'),TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name),description=VALUES(description),category=VALUES(category),quantity=VALUES(quantity),minimum_stock=VALUES(minimum_stock),location_id=VALUES(location_id),active=VALUES(active);

INSERT INTO stock_movements(product_id,movement_type,quantity,previous_quantity,new_quantity,location_id,reason,reference,created_at)
SELECT id,'IN',120,0,120,location_id,'Initial demo inventory','DEMO-001',NOW()-INTERVAL 8 DAY FROM products WHERE sku='BOX-S-001'
UNION ALL SELECT id,'IN',25,0,25,location_id,'Supplier delivery','PO-1042',NOW()-INTERVAL 6 DAY FROM products WHERE sku='TAPE-CLR-48'
UNION ALL SELECT id,'OUT',7,25,18,location_id,'Packing station replenishment','REQ-220',NOW()-INTERVAL 3 DAY FROM products WHERE sku='TAPE-CLR-48'
UNION ALL SELECT id,'IN',15,30,45,location_id,'Supplier delivery','PO-1048',NOW()-INTERVAL 2 DAY FROM products WHERE sku='GLOVE-M-100'
UNION ALL SELECT id,'OUT',5,13,8,location_id,'Customer shipment','SO-3011',NOW()-INTERVAL 1 DAY FROM products WHERE sku='LABEL-100X150'
UNION ALL SELECT id,'ADJUSTMENT',2,6,4,location_id,'Cycle count correction','COUNT-07',NOW()-INTERVAL 4 HOUR FROM products WHERE sku='MARKER-BLK';
