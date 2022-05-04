package app.domain.purchase

import app.domain.rating.Rating

case class Purchase(
                     buyerId: BuyerId,
                     shopId: ShopId,
                     productId: ProductId,
                     rating: Rating
                   )