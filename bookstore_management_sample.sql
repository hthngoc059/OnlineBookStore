USE bookstore_management;

INSERT INTO users (username, email, password, phone_number, role) VALUES
('admin', 'admin@gmail.com', '$2a$10$BjL2FQTAQorfUpc2xgag/.sJAbqy9ZLGCxCo.taFRs22XqWEcz5.y', '0987654321', 'admin'),
('john_doe', 'john@gmail.com', '$2a$10$BjL2FQTAQorfUpc2xgag/.sJAbqy9ZLGCxCo.taFRs22XqWEcz5.y', '0901234567', 'user'),
('lisa_tran', 'lisa@gmail.com', '$2a$10$BjL2FQTAQorfUpc2xgag/.sJAbqy9ZLGCxCo.taFRs22XqWEcz5.y', '0934567890', 'user'),
('mike_nguyen', 'mike@gmail.com', '$2a$10$BjL2FQTAQorfUpc2xgag/.sJAbqy9ZLGCxCo.taFRs22XqWEcz5.y', '0912345678', 'user'),
('sarah_phan', 'sarah@gmail.com', '$2a$10$BjL2FQTAQorfUpc2xgag/.sJAbqy9ZLGCxCo.taFRs22XqWEcz5.y', '0976543210', 'user');

INSERT INTO genres (genre_name) VALUES
("Thiếu Nhi"), 
("Văn Học"),
("Giáo Khoa - Tham Khảo"),
("Manga - Comic"),
("Tâm Lý - Kỹ Năng Sống"), 
("Khoa Học Kỹ Thuật"),
("Huyền Bí - Giả Tưởng - Kinh Dị"),
("Giáo Trình"),
("Kinh Tế"),
("Sách Học Ngoại Ngữ");

INSERT INTO books (cover_image_url, title, author, description, ISBN, publisher, published_year, language, price, stock_quantity, slug, format, is_available) VALUES
('https://i.ibb.co/BKq7bMTg/dac-nhan-tam-1-638.webp', 'Đắc Nhân Tâm', 'Dale Carnegie', 'Nghệ thuật thu phục lòng người và gây ảnh hưởng', '9786043949247', 'NXB Văn Học', 2023, 'Tiếng Việt', 86000, 150, 'dac-nhan-tam', 'paperback', TRUE),
('https://nhungcuonsachhay.com/wp-content/uploads/2021/09/Nha-Gia-Kim-Paulo-Coelho.jpg', 'Nhà Giả Kim', 'Paulo Coelho', 'Hành trình tìm kiếm kho báu và ý nghĩa cuộc sống', '8935235247376', 'NXB Hội Nhà Văn', 2025, 'Tiếng Việt', 95000, 200, 'nha-gia-kim', 'paperback', TRUE),
('https://cdn1.fahasa.com/media/catalog/product/9/7/9781847941831.jpg', 'Atomic Habits', 'James Clear', 'A revolutionary system to get 1 per cent better every day', '9781847941831', 'Random House Business', 2018, 'Tiếng Anh', 628000, 120, 'atomic-habits', 'paperback', TRUE),
('https://i.ibb.co/jkTMxKd9/harry-potter-va-hon-da-phu-thuy-tap-1.webp', 'Harry Potter và Hòn Đá Phù Thủy Tập 1', 'J.K. Rowling', 'Cuộc phiêu lưu kỳ diệu của cậu bé phù thủy', '8934974179672', 'NXB Trẻ', 2022, 'Tiếng Việt', 150000, 100, 'harry-potter-va-hon-da-phu-thuy-1', 'paperback', TRUE),
('https://i.ibb.co/2YSj7ykx/3000-tu-vung-tieng-trung-thong-dung.webp', '3000 Từ Vựng Tiếng Trung Thông Dụng', 'Kaixin', 'tổng hợp những từ vựng tếng Trung được sử dụng với tần suất nhiều nhất trong cuộc sống hàng ngày và trong một số kỳ thi quốc tế thường dùng', '8935246931950', 'NXB Hồng Đức', 2021, 'Tiếng Việt', 120000, 200, '3000-tu-vung-tieng-trung-thong-dung', 'paperback', TRUE),
('https://i.ibb.co/Jwh5VWbR/harry-potter-tap-2.webp', 'Harry Potter và Phòng Chứa Bí Mật', 'J.K. Rowling', 'Tập 2 của series Harry Potter với những bí ẩn tại Hogwarts', '8934974182290', 'NXB Trẻ', 2022, 'Tiếng Việt', 170000, 4, 'harry-potter-phong-chua-bi-mat', 'paperback', TRUE),
('https://i.ibb.co/TMmgS86S/7-thoi-quen.webp', '7 Thói Quen Hiệu Quả', 'Stephen R. Covey', 'Những thói quen để đạt thành công và hạnh phúc bền vững', '8935280400733', 'NXB Tổng Hợp TP.HCM', 2022, 'Tiếng Việt', 250000, 0, '7-thoi-quen-hieu-qua', 'hardcover', FALSE),
('https://i.ibb.co/JRYWqLtg/nghi-giau-lam-giau.jpg', 'Nghĩ Giàu Làm Giàu', 'Napoleon Hill', 'Bí quyết làm giàu từ triệu phú Andrew Carnegie', '8935086844342', 'NXB Tổng Hợp TPHCM', 2020, 'Tiếng Việt', 110000, 200, 'nghi-giau-lam-giau', 'paperback', TRUE),
('https://i.ibb.co/wZv8syFd/song-von-don-thuan-01.webp', 'Sống Vốn Đơn Thuần', 'Phong Tử Khải', 'Tản văn về cuộc sống và những điều bình dị', '8935212310579', 'NXB Hà Nội', 2020, 'Tiếng Việt', 169000, 50, 'song-von-don-thuan', 'hardcover', TRUE),
('https://i.ibb.co/qFXjLNFz/clean-code.jpg', 'Clean Code - Mã Sạch Và Con Đường Trở Thành Lập Trình Viên Giỏi', 'Robert C. Martin', 'Nguyên tắc viết code sạch và bảo trì phần mềm', '8936107813361', 'NXB Dân Trí', 2023, 'Tiếng Việt', 386000, 30, 'clean-code', 'paperback', TRUE),
('https://i.ibb.co/BKrWZNVf/da-vinci.webp', 'Mật Mã Da Vinci', 'Dan Brown', 'Bí ẩn về Chén Thánh và những âm mưu lịch sử', '9781400079179', 'NXB Văn Hóa - Thông Tin', 2003, 'Tiếng Việt', 125000, 110, 'mat-ma-da-vinci', 'paperback', TRUE),
('https://i.ibb.co/mV2bnk5y/phomat.webp', 'Ai Lấy Miếng Pho Mát Của Tôi', 'Spencer Johnson', 'Câu chuyện về sự thay đổi trong công việc và cuộc sống', '8935270703554', 'NXB Tổng hợp thành phố HCM', 2021, 'Tiếng Việt', 68000, 180, 'ai-lay-mat-mieng-pho-mat', 'paperback', TRUE),
('https://i.ibb.co/nNJp4gfq/luoc-su-loai-nguoi.webp', 'Sapiens: Lược Sử Loài Người', 'Yuval Noah Harari', 'Hành trình phát triển của nhân loại từ thời tiền sử', '9780062316097', 'NXB Tri Thức', 2022, 'Tiếng Việt', 299000, 65, 'sapiens-luoc-su-loai-nguoi', 'paperback', TRUE),
('https://i.ibb.co/0VVBnhz0/doraemon-tap-1.webp', 'Doraemon Tập 1', 'Fujiko F. Fujio', 'Chú mèo máy đến từ tương lai với những bảo bối kỳ diệu', '8935235210561', 'NXB Kim Đồng', 2023, 'Tiếng Việt', 22000, 300, 'doraemon-tap-1', 'paperback', TRUE),
('https://i.ibb.co/MDLK56pX/tieng-anh-gen-z.webp', 'Tiếng Anh Gen Z Trong Giao Tiếp - Tiếng Anh Giao Tiếp Dễ Nhớ Cho Người Lười', 'Châm', 'Tiếng Anh Gen Z Trong Giao Tiếp - Tiếng Anh Giao Tiếp Dễ Nhớ Cho Người Lười', '8935246940129', 'NXB Dân Trí', 2025, 'Tiếng Việt', 135000, 140, 'tieng-anh-gen-z', 'paperback', TRUE);

INSERT INTO book_genre (book_id, genre_id) VALUES 
(1, 5), 
(2, 2),
(3, 5),
(4, 7), (4, 1),
(5, 10),
(6, 1), (6, 7),
(7, 5),
(8, 5), (8, 9),
(9, 2),
(10, 6), (10, 8),
(11, 2), (11, 7),
(12, 5),
(13, 2), (13, 6),
(14, 1), (14, 4),
(15, 10);

INSERT INTO wishlists (user_id) VALUES (2), (3);
INSERT INTO wishlists (user_id) VALUES (4), (5);

INSERT INTO wishlist_items (wishlist_id, book_id) VALUES
(1, 2), (1, 5), (1, 3),   
(2, 1), (2, 3), (2, 4),
(3, 10), (3, 3), (3, 7), (3, 13),
(4, 14), (4, 4), (4, 6), (4, 2); 

INSERT INTO addresses (user_id, full_name, phone, address_line, ward, district, city, is_default) VALUES
(2, 'John Doe', '0901234567', '123 Đường Lê Lợi', 'Phường Bến Nghé', 'Quận 1', 'TP. Hồ Chí Minh', TRUE),
(2, 'John Doe', '0901234567', '456 Đường Nguyễn Huệ', 'Phường Bến Nghé', 'Quận 1', 'TP. Hồ Chí Minh', FALSE),
(3, 'Lisa Trần', '0934567890', '567 Đường Trần Duy Hưng', 'Phường Trung Hòa', 'Quận Cầu Giấy', 'Hà Nội', TRUE),
(4, 'Mike Nguyễn', '0912345678', '789 Đường Cách Mạng Tháng 8', 'Phường 11', 'Quận 3', 'TP. Hồ Chí Minh', TRUE),
(4, 'Mike Nguyễn', '0912345678', '123 Đường Phạm Văn Đồng', 'Phường Linh Đông', 'Thành phố Thủ Đức', 'TP. Hồ Chí Minh', FALSE),
(5, 'Sarah Phan', '0976543210', '456 Đường Lê Văn Lương', 'Phường Nhân Chính', 'Quận Thanh Xuân', 'Hà Nội', TRUE),
(5, 'Sarah Phan', '0976543210', '789 Đường Nguyễn Văn Linh', 'Phường Vĩnh Tuy', 'Quận Hai Bà Trưng', 'Hà Nội', FALSE);

INSERT INTO orders (user_id, address_id, total_amount, discount_amount, final_amount, status, payment_status) VALUES
(2, 1, 420000, 20000, 400000, 'delivered', 'paid'),
(2, 1, 185000, 0, 185000, 'delivered', 'paid'),
(3, 3, 270000, 0, 270000, 'shipping', 'paid'),
(2, 1, 420000, 42000, 378000, 'delivered', 'paid'),
(3, 3, 296000, 0, 296000, 'shipping', 'paid'),
(4, 4, 850000, 85000, 765000, 'confirmed', 'paid'),
(5, 6, 250000, 25000, 225000, 'pending', 'unpaid'),
(2, 2, 125000, 0, 125000, 'cancelled', 'refunded'),
(3, 3, 210000, 21000, 189000, 'delivered', 'paid'),
(4, 5, 68000, 0, 68000, 'shipping', 'paid'),
(5, 7, 346000, 30000, 316000, 'confirmed', 'paid'),
(2, 1, 99000, 0, 99000, 'delivered', 'paid'),
(3, 3, 420000, 42000, 378000, 'pending', 'unpaid');


INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES
(1, 3, 1, 420000),     
(2, 5, 1, 185000),    
(3, 1, 2, 89000),      
(3, 4, 1, 92000);

INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES
(4, 1, 2, 86000),   -- Đắc Nhân Tâm x2
(4, 2, 1, 95000);   -- Nhà Giả Kim

INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES
(5, 7, 1, 148000),  -- 7 Thói Quen Hiệu Quả
(5, 12, 1, 68000);  -- Cá Và Chuột

INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES
(6, 10, 1, 850000); -- Clean Code

INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES
(7, 14, 3, 25000),  -- Doraemon Tập 1 x3
(7, 9, 1, 89000);   -- Sống Vốn Đơn Thuần 

INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES
(8, 11, 1, 125000); -- Mật Mã Da Vinci

INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES
(9, 5, 2, 120000);  -- 3000 Từ Vựng Tiếng Trung x2

INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES
(10, 12, 1, 68000); -- Cá Và Chuột

INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES
(11, 13, 1, 210000), -- Sapiens
(11, 14, 2, 25000),  -- Doraemon x2
(11, 6, 1, 160000);  -- Harry Potter 2

-- Order 12: User 2 mua 1 sách Nghĩ Giàu Làm Giàu (8)
INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES
(12, 8, 1, 99000);  -- Nghĩ Giàu Làm Giàu

-- Order 13: User 3 mua 1 sách Harry Potter 1 (4), 2 sách Atomic Habits (3)
INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES
(13, 4, 1, 150000),  -- Harry Potter 1
(13, 3, 2, 628000);  -- Atomic Habits x2

INSERT INTO carts (user_id) VALUES (2), (3);

INSERT INTO cart_items (cart_id, book_id, quantity) VALUES
(1, 5, 1),   
(1, 3, 2),  
(2, 2, 1),   
(2, 4, 1);

INSERT INTO payments (order_id, payment_method, payment_status, transaction_id) VALUES
(1, 'momo', 'completed', 'MOMO_20231201_001'),
(2, 'vnpay', 'completed', 'VNPAY_20231205_002'),
(3, 'banking', 'completed', 'BANK_20231210_003'),
(4, 'momo', 'completed', 'MOMO_20250115_004'),
(5, 'vnpay', 'completed', 'VNPAY_20250116_005'),
(6, 'banking', 'completed', 'BANK_20250117_006'),
(7, 'momo', 'pending', 'MOMO_20250118_007'),
(8, 'vnpay', 'refunded', 'VNPAY_20250119_008'),
(9, 'banking', 'completed', 'BANK_20250120_009'),
(10, 'momo', 'completed', 'MOMO_20250121_010'),
(11, 'vnpay', 'completed', 'VNPAY_20250122_011'),
(12, 'banking', 'completed', 'BANK_20250123_012'),
(13, 'momo', 'pending', 'MOMO_20250124_013');

INSERT INTO reviews (user_id, book_id, rating, comment) VALUES
(2, 3, 5, 'Cuốn sách tuyệt vời, giúp tôi thay đổi thói quen rất nhiều!'),
(2, 5, 4, 'Kiến thức rộng và thú vị, nhưng đôi chỗ hơi khô khan.'),
(3, 1, 5, 'Kinh điển về giao tiếp, ai cũng nên đọc một lần.'),
(3, 4, 5, 'Con trai tôi rất thích, đọc đi đọc lại nhiều lần.'),
(4, 10, 5, 'Clean Code là cuốn sách mà bất kỳ lập trình viên nào cũng nên đọc. Nội dung chất lượng, ví dụ thực tế và dễ áp dụng.'),
(4, 12, 4, 'Cá Và Chuột - câu chuyện ngắn nhưng ý nghĩa về sự thay đổi. Phù hợp cho những ai đang lo sợ trước sự đổi mới.'),
(5, 14, 5, 'Doraemon - tuổi thơ của tôi! Con gái tôi rất thích, đọc đi đọc lại nhiều lần. Tranh vẽ đẹp, giấy tốt.'),
(5, 13, 5, 'Sapiens là một kiệt tác. Cuốn sách mở mang tầm nhìn về lịch sử loài người. Tuy dày nhưng đọc rất cuốn.'),
(2, 8, 4, 'Nghĩ Giàu Làm Giàu có nhiều nguyên lý giá trị, nhưng cách viết hơi cũ. Tuy nhiên các bài học vẫn còn nguyên giá trị.');

INSERT INTO discounts (code, description, discount_type, discount_value, start_date, end_date, max_usage, used_count, is_active) VALUES
('WELCOME10', 'Giảm 10% cho đơn hàng đầu tiên', 'percent', 10, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1000, 245, TRUE),
('SUMMER50', 'Giảm 50k cho đơn từ 300k', 'fixed', 50000, '2024-06-01 00:00:00', '2024-08-31 23:59:59', 500, 89, TRUE),
('AUTUMN20', 'Giảm 20% toàn bộ sách văn học', 'percent', 20, '2025-09-01 00:00:00', '2025-10-31 23:59:59', 300, 45, TRUE),
('FREESHIP30', 'Giảm 30k phí vận chuyển', 'fixed', 30000, '2025-08-15 00:00:00', '2025-09-15 23:59:59', 200, 67, TRUE),
('FLASHSALE', 'Flash sale giảm 15% đơn từ 200k', 'percent', 15, '2025-08-01 00:00:00', '2025-08-31 23:59:59', 100, 98, TRUE),
('MEMBERS50', 'Thành viên thân thiết giảm 50k', 'fixed', 50000, '2025-07-01 00:00:00', '2025-12-31 23:59:59', 500, 123, FALSE),
('SACHHAY', 'Mã tri ân - giảm 10k', 'fixed', 10000, '2025-08-20 00:00:00', '2025-09-20 23:59:59', 1000, 215, TRUE);

INSERT INTO book_discount (book_id, discount_id) VALUES
(1, 1),  
(2, 2),   
(4, 2),
(6, 2),  
(7, 1),   
(10, 5),  
(11, 7),  
(14, 6),  
(15, 5);  


INSERT INTO notifications (user_id, title, message, is_read) VALUES
(2, 'Đơn hàng đã giao thành công', 'Đơn hàng #1 đã được giao đến bạn. Cảm ơn bạn đã mua sắm!', TRUE),
(2, 'Mã giảm giá mới', 'Chào mừng năm mới, bạn nhận được mã WELCOME10 giảm 10%', FALSE),
(3, 'Đơn hàng đang được giao', 'Đơn hàng #3 của bạn đang trên đường giao, vui lòng chú ý điện thoại', FALSE),
(4, 'Chào mừng đến với Bookstore', 'Cảm ơn bạn đã đăng ký thành viên. Nhận ngay mã giảm giá WELCOME10', TRUE),
(4, 'Đơn hàng #6 đã xác nhận', 'Đơn hàng Clean Code của bạn đã được xác nhận và đang được đóng gói', TRUE),
(5, 'Khuyến mãi cuối tuần', 'Giảm 20% tất cả sách thiếu nhi từ thứ 7 đến CN', FALSE),
(5, 'Đơn hàng #7 chờ thanh toán', 'Vui lòng thanh toán đơn hàng để được xác nhận sớm nhất', FALSE),
(2, 'Hoàn tiền thành công', 'Đơn hàng #8 đã được hoàn tiền vào tài khoản VNPay của bạn', TRUE),
(3, 'Sách mới về - Bán chạy', 'Atomic Habits - Cuốn sách thay đổi thói quen đang có ưu đãi 10%', FALSE),
(4, 'Gợi ý sách cho bạn', 'Dựa trên sở thích lập trình, chúng tôi gợi ý cuốn "Clean Code"', TRUE),
(5, 'Flash sale 1 tiếng', 'Siêu giảm giá 50% sách Doraemon trong 1 tiếng tới', FALSE),
(2, 'Tích điểm thưởng', 'Bạn đã tích lũy được 500 điểm. Đổi ngay ưu đãi hấp dẫn', FALSE),
(3, 'Đánh giá sách nhận quà', 'Đánh giá sách bạn đã mua để nhận mã giảm giá 20k', FALSE);
