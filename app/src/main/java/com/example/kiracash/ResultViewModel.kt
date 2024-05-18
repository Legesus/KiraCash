import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kiracash.data.AppDatabase
import com.example.kiracash.data.ReceiptItem
import com.example.kiracash.data.WalletEntity
import com.example.kiracash.data.WalletItemJoin
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData

class ResultViewModel(application: Application) : AndroidViewModel(application) {

    private val receiptItemDao = AppDatabase.getDatabase(application).receiptItemDao()
    private val walletDao = AppDatabase.getDatabase(application).walletDao()
    private val walletItemJoinDao = AppDatabase.getDatabase(application).walletItemJoinDao()


    fun parseAndSaveReceiptItems(text: String) {
        val lines = text.split("\n")
        for (line in lines) {
            val parts = line.split(" ")
            if (parts.size >= 2) {
                val itemName = parts.dropLast(1).joinToString(" ")
                val itemPrice = parts.last().toDoubleOrNull()
                if (itemPrice != null) {
                    viewModelScope.launch {
                        val receiptItem = ReceiptItem(itemName = itemName, itemPrice = itemPrice)
                        receiptItemDao.insert(receiptItem)
                    }
                }
            }
        }
    }

    fun getWallets() = walletDao.getWallets()

    fun insertWallet(walletName: String) {
        viewModelScope.launch {
            val wallet = WalletEntity(walletName = walletName)
            walletDao.insert(wallet)
        }
    }

    fun assignItemToWallet(itemId: Long, walletId: Long) {
        viewModelScope.launch {
            val join = WalletItemJoin(walletId, itemId)
            walletItemJoinDao.insert(join)
        }
    }


    fun getReceiptItems(): LiveData<List<ReceiptItem>> {
        return receiptItemDao.getReceiptItems()
    }
}