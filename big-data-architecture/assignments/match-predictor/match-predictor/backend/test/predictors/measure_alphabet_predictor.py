from unittest import TestCase

from matchpredictor.matchresults.results_provider import validation_results
from matchpredictor.evaluation.evaluator import Evaluator
from matchpredictor.predictors.alphabet_predictor import AlphabetPredictor
from test.predictors import csv_location


class TestAlphabetPredictor(TestCase):
    def test_accuracy(self) -> None:
        validation_data = validation_results(csv_location, 2019)
        accuracy, _ = Evaluator(AlphabetPredictor()).measure_accuracy(validation_data)

        self.assertGreaterEqual(accuracy, .33)
