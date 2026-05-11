export type FoodItem = {
  id: string
  title: string
  imageUrl?: string
  tags: string[]
  heat: number
  from: number
  category: string
  cornerTag?: string
}

export const items: FoodItem[] = [
  {
    id: '1',
    title: '招牌厚切炒酸奶水果捞',
    imageUrl: '',
    tags: ['酸甜', '冰爽'],
    heat: 9856,
    from: 18.5,
    category: 'dessert',
    cornerTag: '甜品',
  },
  {
    id: '2',
    title: '脏脏黑糖珍珠奶茶',
    imageUrl: '',
    tags: ['网红', '小甜'],
    heat: 12540,
    from: 12.9,
    category: 'drink',
    cornerTag: '饮品',
  },
  {
    id: '3',
    title: '香辣鸡腿堡双人套餐',
    imageUrl: '',
    tags: ['含薯', '超值'],
    heat: 8560,
    from: 29.9,
    category: 'fast',
    cornerTag: '快餐',
  },
  {
    id: '4',
    title: '日式肥牛寿喜烧盖饭',
    imageUrl: '',
    tags: ['温热食', '肥牛'],
    heat: 6540,
    from: 32,
    category: 'rice',
    cornerTag: '盖饭',
  },
]
