fragment nào cần update dữ liệu của task và list á ( update trên firebase sau đó cập nhật UI), ví dụ update task info, update list info ,...
=> fragment đó cần implement interface DataUpdateListener


Step 1:  ở hàm onViewCreated, code như dưới

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseManager.addDataUpdateListener(this)



Step 2: thêm hàm onDestroyView

override fun onDestroyView() {
        super.onDestroyView()
        FirebaseManager.removeDataUpdateListener(this)
}



Step 3: override hàm updateData (vì implement DataUpdateListener)

override fun updateData() {
        // Gán dữ liệu mới vào cái TextView,...
}


Reference: có thể tham khảo code ở fragment ListOfTask