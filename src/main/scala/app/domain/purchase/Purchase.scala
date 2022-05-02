package app.domain.purchase

case class Purchase(
                     buyerId: BuyerId,
                     shopId: ShopId,
                     productId: ProductId,
                     rating: Rating
                   ) {
  def toProductRating: ProductRating =
    ProductRating(productId = productId, rating = rating)
}