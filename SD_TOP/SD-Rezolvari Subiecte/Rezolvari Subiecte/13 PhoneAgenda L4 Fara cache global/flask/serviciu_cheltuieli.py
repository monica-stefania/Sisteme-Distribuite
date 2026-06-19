
class CheltuieliService:

	def __init__(self):
		self.bani_mama = 50
		self.bani_tata = 100
		self.bani_copil = 0

	def depunde(self, amount, who):
		if who == "mama":
			self.bani_mama += amount
		elif who == "tata":
			self.bani_tata += amount
		elif who == "copil":
			self.bani_copil += amount

	def cheltuie(self, amount, who):
		if who == "mama":
			self.bani_mama -= amount
		elif who == "tata":
			self.bani_tata -= amount
		elif who == "copil":
			self.bani_copil -= amount

	def get_bani_mama(self):
		return self.bani_mama

	def get_bani_tata(self):
		return self.bani_tata

	def get_bani_copil(self):
		return self.bani_copil